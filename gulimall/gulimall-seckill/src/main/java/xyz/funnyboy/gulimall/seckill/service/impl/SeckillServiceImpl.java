package xyz.funnyboy.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.constant.SeckillConstant;
import xyz.funnyboy.common.to.seckill.SeckillOrderTO;
import xyz.funnyboy.common.to.seckill.SeckillSkuRedisTO;
import xyz.funnyboy.common.to.seckill.SkuInfoTO;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.common.vo.auth.MemberRespVO;
import xyz.funnyboy.gulimall.seckill.feign.CouponFeignService;
import xyz.funnyboy.gulimall.seckill.feign.ProductFeignService;
import xyz.funnyboy.gulimall.seckill.interceptor.LoginUserInterceptor;
import xyz.funnyboy.gulimall.seckill.service.SeckillService;
import xyz.funnyboy.gulimall.seckill.vo.SeckillSessionWithSkusTO;
import xyz.funnyboy.gulimall.seckill.vo.SeckillSkuVO;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-20 18:04:40
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService
{
    private static final String SECKIL_EXCHANGE = "order-event-exchange";
    private static final String SECKILL_ROUTING_KEY = "order.seckill.order";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 上架最近三天需要秒杀的商品
     */
    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 扫描最近三天数据库需要参与秒杀的活动
        final R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() != 0) {
            log.warn("未获取到最近三天秒杀活动");
            return;
        }

        final List<SeckillSessionWithSkusTO> sessions = r.getData(new TypeReference<List<SeckillSessionWithSkusTO>>() {});
        if (CollectionUtils.isEmpty(sessions)) {
            log.warn("未获取到最近三天秒杀活动");
            return;
        }

        // 上架场次信息
        saveSessionInfos(sessions);

        // 上架商品信息
        saveSessionSkuInfo(sessions);
    }

    @Override
    public List<SeckillSkuRedisTO> getCurrentSeckillSkus() {
        // 查询当前时间所属的秒杀场次
        final long currentTime = System.currentTimeMillis();
        // 1、查询所有秒杀场次的key => seckill:sessions:*
        final Set<String> keys = redisTemplate.keys(SeckillConstant.SESSION_CACHE_PREFIX + "*");
        for (String key : Objects.requireNonNull(keys)) {
            final String[] time = key
                    .replace(SeckillConstant.SESSION_CACHE_PREFIX, "")
                    .split("_");
            final long startTime = Long.parseLong(time[0]);
            final long endTime = Long.parseLong(time[1]);
            // 2、判断是否处于该场次
            if (currentTime >= startTime && currentTime <= endTime) {
                // 3、查询场次信息
                final List<String> sessionIdSkuIds = redisTemplate
                        // List<sessionId_skuId>
                        .opsForList()
                        // list 范围内 100 条数据
                        .range(key, -100, 100);
                // 4、批量获取商品信息
                final BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
                final List<String> skus = skuOps.multiGet(Objects.requireNonNull(sessionIdSkuIds));
                if (!CollectionUtils.isEmpty(skus)) {
                    // 5、将商品信息反序列成对象
                    return skus
                            .stream()
                            .map(sku -> JSON.parseObject(sku, SeckillSkuRedisTO.class))
                            .collect(Collectors.toList());
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTO getSkuSeckilInfo(Long skuId) {
        // 匹配查询当前商品的秒杀信息
        final BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
        final Set<String> keys = skuOps.keys();
        if (CollectionUtils.isEmpty(keys)) {
            log.error("未获取到秒杀商品信息...");
            return null;
        }

        // 获取所有商品的key：sessionId_
        String lastIndex = "_" + skuId;
        for (String key : keys) {
            // 商品id匹配成功
            if (key.lastIndexOf(lastIndex) == -1) {
                log.error("秒杀商品信息匹配失败...");
                continue;
            }
            // 进行序列化
            final SeckillSkuRedisTO seckillSkuRedisTO = JSONObject.parseObject(skuOps.get(key), SeckillSkuRedisTO.class);
            // 当前时间小于截止时间
            final long currentTime = System.currentTimeMillis();
            final Long endTime = Objects
                    .requireNonNull(seckillSkuRedisTO)
                    .getEndTime();
            if (currentTime > endTime) {
                log.error("当前时间超过截止时间...");
                continue;
            }
            // 当前时间大于开始时间
            final Long startTime = Objects
                    .requireNonNull(seckillSkuRedisTO)
                    .getStartTime();
            if (currentTime < startTime) {
                // 返回预告信息，不返回随机码
                log.error("当前时间未到开始时间...");
                seckillSkuRedisTO.setRandomCode(null);
                return seckillSkuRedisTO;
            }
            return seckillSkuRedisTO;
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        // 1、校验登录状态
        final long start = System.currentTimeMillis();
        // 获取当前用户信息
        final MemberRespVO user = LoginUserInterceptor.loginUser.get();

        // 获取当前秒杀商品的详细信息
        final BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
        final String json = skuOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            // 这一步已经默认校验了场次+商品，如果为空表示校验失败
            long end = System.currentTimeMillis();
            log.error("当前商品{}秒杀信息为空，耗时...{}", killId, (end - start));
            return null;
        }
        // 反序列化商品信息
        final SeckillSkuRedisTO skuInfo = JSONObject.parseObject(json, SeckillSkuRedisTO.class);

        // 2、校验秒杀时间
        Long startTime = skuInfo.getStartTime();
        Long endTime = skuInfo.getEndTime();
        long currentTime = System.currentTimeMillis();
        if (currentTime < startTime || currentTime > endTime) {
            long end = System.currentTimeMillis();
            log.error("当前商品{}不在秒杀时间内，耗时...{}", killId, (end - start));
            return null;
        }

        // 3、校验随机码
        final String randomCode = skuInfo.getRandomCode();
        if (!randomCode.equals(key)) {
            long end = System.currentTimeMillis();
            log.error("当前商品{}随机码不一致，耗时...{}", killId, (end - start));
            return null;
        }

        // 4、校验信号量（库存是否充足）、校验购物数量是否限购
        // 获取每人限购数量
        final Integer seckillLimit = skuInfo.getSeckillLimit();
        // 获取信号量
        final String seckillCount = redisTemplate
                .opsForValue()
                .get(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode);
        final int count = Integer.parseInt(Objects.requireNonNull(seckillCount));
        if (num <= 0 || num > seckillLimit || count <= num) {
            long end = System.currentTimeMillis();
            log.error("当前商品{}库存不足或限购数量不足，耗时...{}", killId, (end - start));
            return null;
        }

        // 5、校验是否重复秒杀（幂等性）【秒杀成功后占位，userId-sessionId-skuId】
        final String userKey = SeckillConstant.SECKILL_USER_PREFIX + user.getId() + "_" + killId;
        // 自动过期时间(活动结束时间 - 当前时间)
        final long ttl = endTime - currentTime;
        final Boolean isRepeat = redisTemplate
                .opsForValue()
                .setIfAbsent(userKey, num.toString(), ttl, TimeUnit.MICROSECONDS);
        if (isRepeat == null || !isRepeat) {
            long end = System.currentTimeMillis();
            log.error("当前商品{}重复秒杀，耗时...{}", killId, (end - start));
            return null;
        }

        // 6、占位成功，扣减信号量（防止超卖）
        final boolean isAcquire = redissonClient
                .getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode)
                .tryAcquire(num, 100, TimeUnit.MILLISECONDS);
        if (!isAcquire) {
            long end = System.currentTimeMillis();
            log.error("当前商品{}扣减信号量失败，耗时...{}", killId, (end - start));
            return null;
        }

        // 信号量扣减成功，秒杀成功，快速下单
        // 7.发送消息，创建订单号和订单信息
        // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
        final String orderSn = IdWorker.getTimeId();
        final SeckillOrderTO seckillOrderTO = new SeckillOrderTO();
        seckillOrderTO.setOrderSn(orderSn);
        seckillOrderTO.setMemberId(user.getId());
        seckillOrderTO.setPromotionSessionId(skuInfo.getPromotionSessionId());
        seckillOrderTO.setSkuId(skuInfo.getSkuId());
        seckillOrderTO.setSeckillPrice(skuInfo.getSeckillPrice());
        seckillOrderTO.setNum(num);
        // 需要保证可靠消息，发送者确认+消费者确认（本地事务的形式）
        rabbitTemplate.convertAndSend(SECKIL_EXCHANGE, SECKILL_ROUTING_KEY, seckillOrderTO);

        long end = System.currentTimeMillis();
        log.info("当前商品{}秒杀成功，耗时...{}", killId, (end - start));
        return orderSn;
    }

    /**
     * 缓存秒杀场次信息
     *
     * @param sessions 会话
     */
    private void saveSessionInfos(List<SeckillSessionWithSkusTO> sessions) {
        sessions.forEach(session -> {
            // 1、遍历场次
            final long startTime = session
                    .getStartTime()
                    .getTime();
            final long endTime = session
                    .getEndTime()
                    .getTime();
            final String key = SeckillConstant.SESSION_CACHE_PREFIX + startTime + "_" + endTime;

            // 2、判断场次是否已上架（幂等性）
            final Boolean hasKey = redisTemplate.hasKey(key);
            // 未上架
            if (hasKey == null || !hasKey) {
                // 3、封装场次信息
                final List<String> skuIds = session
                        .getRelationSkus()
                        .stream()
                        .map(item -> item.getPromotionSessionId() + "_" + item
                                .getSkuId()
                                .toString())
                        .collect(Collectors.toList());

                // 4、上架
                redisTemplate
                        .opsForList()
                        .leftPushAll(key, skuIds);
            }
        });
    }

    /**
     * 缓存活动的关联商品信息
     *
     * @param sessions 会话
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkusTO> sessions) {
        // 1、查询所有商品信息
        final List<Long> skuIds = sessions
                .stream()
                .flatMap(session -> session
                        .getRelationSkus()
                        .stream()
                        .map(SeckillSkuVO::getSkuId))
                .collect(Collectors.toList());
        final R r = productFeignService.getSkuInfos(skuIds);
        if (r.getCode() != 0) {
            log.error("远程调用商品服务失败");
            return;
        }

        // 2、将查询结果封装成Map集合
        final Map<Long, SkuInfoTO> skuMap = r
                .getData(new TypeReference<List<SkuInfoTO>>() {})
                .stream()
                .collect(Collectors.toMap(SkuInfoTO::getSkuId, Function.identity(), (r1, r2) -> r2));

        // 3、绑定秒杀商品hash
        final BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);

        // 4、遍历场次
        sessions.forEach(session -> {
            // 5、遍历商品
            final List<SeckillSkuVO> seckillSkuVOList = session.getRelationSkus();
            seckillSkuVOList.forEach(seckillSkuVO -> {
                // 6、判断商品是否已上架（幂等性）
                final Long skuId = seckillSkuVO.getSkuId();
                final String skuKey = seckillSkuVO
                        .getPromotionSessionId()
                        .toString() + "_" + skuId.toString();
                // 未上架
                if (!operations.hasKey(skuKey)) {
                    // 7、封装商品信息
                    final SeckillSkuRedisTO seckillSkuRedisTO = new SeckillSkuRedisTO();
                    BeanUtils.copyProperties(seckillSkuVO, seckillSkuRedisTO);
                    // 商品详细信息
                    seckillSkuRedisTO.setSkuInfo(skuMap.get(skuId));
                    // 秒杀开始时间
                    seckillSkuRedisTO.setStartTime(session
                            .getStartTime()
                            .getTime());
                    // 秒杀结束时间
                    seckillSkuRedisTO.setEndTime(session
                            .getEndTime()
                            .getTime());
                    // 商品随机码：用户参与秒杀时，请求需要带上随机码（防止恶意攻击）
                    final String token = UUID
                            .randomUUID()
                            .toString()
                            .replace("-", "");
                    seckillSkuRedisTO.setRandomCode(token);

                    // 8、上架商品（序列化成json格式存入Redis中）
                    operations.put(skuKey, JSON.toJSONString(seckillSkuRedisTO));

                    // 9、上架商品的分布式信号量，key：商品随机码 值：库存（限流）
                    final RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + token);
                    // 信号量（扣减成功才进行后续操作，否则快速返回）
                    semaphore.trySetPermits(seckillSkuVO
                            .getSeckillCount()
                            .intValue());
                }
            });
        });
    }
}
