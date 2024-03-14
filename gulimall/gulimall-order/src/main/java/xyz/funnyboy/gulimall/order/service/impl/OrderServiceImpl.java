package xyz.funnyboy.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import xyz.funnyboy.common.to.es.SkuHasStockVO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.common.vo.auth.MemberRespVO;
import xyz.funnyboy.gulimall.order.constant.OrderConstant;
import xyz.funnyboy.gulimall.order.dao.OrderDao;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;
import xyz.funnyboy.gulimall.order.entity.OrderItemEntity;
import xyz.funnyboy.gulimall.order.feign.CartFeignService;
import xyz.funnyboy.gulimall.order.feign.MemberFeignService;
import xyz.funnyboy.gulimall.order.feign.ProductFeignService;
import xyz.funnyboy.gulimall.order.feign.WmsFeignService;
import xyz.funnyboy.gulimall.order.interceptor.LoginUserInterceptor;
import xyz.funnyboy.gulimall.order.service.OrderItemService;
import xyz.funnyboy.gulimall.order.service.OrderService;
import xyz.funnyboy.gulimall.order.to.OrderCreateTO;
import xyz.funnyboy.gulimall.order.vo.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService
{

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WmsFeignService wmsFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    private ThreadLocal<OrderSubmitVO> orderSubmitVOThreadLocal = new ThreadLocal<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params), new QueryWrapper<OrderEntity>());

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVO confirmOrder() throws ExecutionException, InterruptedException {
        final OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        // 通过拦截器获取当前线程的用户信息
        final MemberRespVO memberRespVO = LoginUserInterceptor.threadLocal.get();
        // 获取原请求数据
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 1、远程查询所有的收货地址列表
        final CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            // 每个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            orderConfirmVO.setAddress(memberFeignService.getAddress(memberRespVO.getId()));
        }, executor);

        // 2、远程查询购物车所有选中的购物项
        final CompletableFuture<Void> itemsFuture = CompletableFuture
                .runAsync(() -> {
                    // 每个线程都共享之前的请求数据
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    orderConfirmVO.setItems(cartFeignService.getCurrentUserCartItems());
                }, executor)
                .thenRunAsync(() -> {
                    final List<Long> skuIdList = orderConfirmVO
                            .getItems()
                            .stream()
                            .map(OrderItemVO::getSkuId)
                            .collect(Collectors.toList());
                    final List<SkuHasStockVO> stockVOList = wmsFeignService
                            .hasStock(skuIdList)
                            .getData("data", new TypeReference<List<SkuHasStockVO>>() {});
                    if (!CollectionUtils.isEmpty(stockVOList)) {
                        final Map<Long, Boolean> stocks = stockVOList
                                .stream()
                                .collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
                        orderConfirmVO.setStocks(stocks);
                    }
                }, executor);

        // 3、查询用户积分
        orderConfirmVO.setIntegration(memberRespVO.getIntegration());

        // 4、防重令牌
        final String token = UUID
                .randomUUID()
                .toString()
                .replace("-", "");
        orderConfirmVO.setOrderToken(token);
        stringRedisTemplate
                .opsForValue()
                .set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVO.getId(), token, 10, TimeUnit.MINUTES);

        // 5、返回结果
        CompletableFuture
                .allOf(addressFuture, itemsFuture)
                .get();
        return orderConfirmVO;
    }

    @Override
    public OrderSubmitResponseVO submitOrder(OrderSubmitVO orderSubmitVO) {
        orderSubmitVOThreadLocal.set(orderSubmitVO);
        final OrderSubmitResponseVO orderSubmitResponseVO = new OrderSubmitResponseVO();
        orderSubmitResponseVO.setCode(0);

        final MemberRespVO memberRespVO = LoginUserInterceptor.threadLocal.get();
        // 1、验证令牌 [必须保证原子性] 返回 0 or 1
        final String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        final Long result = stringRedisTemplate.execute(
                // RedisScript<T> script
                new DefaultRedisScript<>(script, Long.class),
                // List<K> keys
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVO.getId()),
                // Object... args
                orderSubmitVO.getOrderToken());
        // 0 令牌删除失败 1删除成功
        if (result == null || result == 0L) {
            // 错误状态码：0——成功
            log.error("令牌验证失败");
            orderSubmitResponseVO.setCode(1);
            return orderSubmitResponseVO;
        }

        // 2、创建订单
        OrderCreateTO orderCreateTO = createOrder();

        // 3、验价格
        final BigDecimal payAmount = Objects
                .requireNonNull(orderCreateTO)
                .getOrder()
                .getPayAmount();
        final BigDecimal payPrice = orderSubmitVO.getPayPrice();
        // 金额对比失败
        if (Math.abs(payPrice
                .subtract(payAmount)
                .doubleValue()) >= 0.01) {
            log.error("金额对比失败");
            orderSubmitResponseVO.setCode(2);
            return orderSubmitResponseVO;
        }

        // 4、保存订单
        saveOrder(orderCreateTO);

        // 5、锁库存
        final List<OrderItemVO> orderItemVOList = orderCreateTO
                .getOrderItems()
                .stream()
                .map(item -> {
                    final OrderItemVO orderItemVO = new OrderItemVO();
                    orderItemVO.setSkuId(item.getSkuId());
                    orderItemVO.setTitle(item.getSkuName());
                    orderItemVO.setCount(item.getSkuQuantity());
                    return orderItemVO;
                })
                .collect(Collectors.toList());
        final WareSkuLockVO wareSkuLockVO = new WareSkuLockVO();
        wareSkuLockVO.setOrderSn(orderCreateTO
                .getOrder()
                .getOrderSn());
        wareSkuLockVO.setLocks(orderItemVOList);
        final R r = wmsFeignService.orderLockStock(wareSkuLockVO);
        // 锁定失败
        if (r.getCode() != 0) {
            log.error("锁定失败");
            orderSubmitResponseVO.setCode(3);
            return orderSubmitResponseVO;
        }
        // 锁定成功
        orderSubmitResponseVO.setOrderEntity(orderCreateTO.getOrder());
        return orderSubmitResponseVO;
    }

    /**
     * 创建订单
     *
     * @return {@link OrderCreateTO}
     */
    private OrderCreateTO createOrder() {
        // 1、生成订单号(mybatis为我们提供的一个可用以生成订单id的方法)
        final String orderSn = IdWorker.getTimeId();
        final OrderEntity orderEntity = buildOrder(orderSn);

        // 2、获取到所有的订单项
        final List<OrderItemEntity> itemEntityList = buildOrderItems(orderSn);

        // 3、验价
        computePrice(orderEntity, itemEntityList);

        // 4、构建订单
        final OrderCreateTO orderCreateTO = new OrderCreateTO();
        orderCreateTO.setOrder(orderEntity);
        orderCreateTO.setOrderItems(itemEntityList);
        return orderCreateTO;
    }

    /**
     * 构建订单
     *
     * @param orderSn 订购 SN
     * @return {@link OrderEntity}
     */
    private OrderEntity buildOrder(String orderSn) {
        final OrderEntity orderEntity = new OrderEntity();

        // 订单信息
        orderEntity.setOrderSn(orderSn);
        orderEntity.setCreateTime(new Date());
        orderEntity.setCommentTime(new Date());
        orderEntity.setReceiveTime(new Date());
        orderEntity.setDeliveryTime(new Date());

        // 用户信息
        final MemberRespVO memberRespVO = LoginUserInterceptor.threadLocal.get();
        orderEntity.setMemberId(memberRespVO.getId());
        orderEntity.setMemberUsername(memberRespVO.getUsername());
        orderEntity.setBillReceiverEmail(memberRespVO.getEmail());

        // 收货地址信息
        final OrderSubmitVO orderSubmitVO = orderSubmitVOThreadLocal.get();
        final R r = wmsFeignService.getFare(orderSubmitVO.getAddrId());
        final FareVO fareVO = r.getData("data", new TypeReference<FareVO>() {});
        final BigDecimal fare = fareVO.getFare();
        final MemberAddressVO address = fareVO.getAddress();
        orderEntity.setFreightAmount(fare);
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverName(address.getName());

        // 订单状态
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }

    /**
     * 构建订单项
     *
     * @param orderSn 订购 SN
     * @return {@link List}<{@link OrderItemEntity}>
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        final List<OrderItemVO> orderItemVOList = cartFeignService.getCurrentUserCartItems();
        if (CollectionUtils.isEmpty(orderItemVOList)) {
            return Collections.emptyList();
        }

        return orderItemVOList
                .stream()
                .map(orderItemVO -> {
                    final OrderItemEntity orderItemEntity = buildOrderItem(orderItemVO);
                    orderItemEntity.setOrderSn(orderSn);
                    return orderItemEntity;
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建订单子项
     *
     * @param orderItemVO 订购项 VO
     * @return {@link OrderItemEntity}
     */
    private OrderItemEntity buildOrderItem(OrderItemVO orderItemVO) {
        final OrderItemEntity orderItemEntity = new OrderItemEntity();

        // 1、商品spu信息
        final Long skuId = orderItemVO.getSkuId();
        final R r = productFeignService.getSpuInfoBySkuId(skuId);
        final SpuInfoVO spuInfoVO = r.getData("data", new TypeReference<SpuInfoVO>() {});
        orderItemEntity.setSpuId(spuInfoVO.getId());
        orderItemEntity.setSpuName(spuInfoVO.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoVO
                .getBrandId()
                .toString());
        orderItemEntity.setCategoryId(spuInfoVO.getCatalogId());

        // 2、商品sku属性
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(orderItemVO.getTitle());
        orderItemEntity.setSkuPic(orderItemVO.getImage());
        orderItemEntity.setSkuPrice(orderItemVO.getPrice());
        orderItemEntity.setSkuQuantity(orderItemVO.getCount());
        // 把一个集合按照指定的字符串进行分割得到一个字符串
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(orderItemVO.getSkuAttr(), ";"));

        // 3、积分信息
        final int integration = orderItemVO
                .getPrice()
                .multiply(BigDecimal.valueOf(orderItemVO.getCount()))
                .intValue();
        orderItemEntity.setGiftGrowth(integration);
        orderItemEntity.setGiftIntegration(integration);

        // 4、优惠金额
        // 商品促销分解金额
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
        // 优惠券优惠分解金额
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
        // 积分优惠分解金额
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        // 最终价格
        final BigDecimal realAmount = orderItemEntity
                .getSkuPrice()
                .multiply(BigDecimal.valueOf(orderItemEntity.getSkuQuantity()))
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realAmount);

        return orderItemEntity;
    }

    /**
     * 计算价格
     *
     * @param orderEntity 次序
     * @param orderItems  订购商品
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal totalPrice = new BigDecimal("0.0");
        // 叠加每一个订单项的金额
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        for (OrderItemEntity orderItem : orderItems) {
            totalPrice = totalPrice.add(orderItem.getRealAmount());
            // 优惠券的金额
            coupon = coupon.add(orderItem.getCouponAmount());
            // 积分优惠的金额
            integration = integration.add(orderItem.getIntegrationAmount());
            // 促销打折的金额
            promotion = promotion.add(orderItem.getPromotionAmount());
            // 购物获取的积分
            gift = gift.add(BigDecimal.valueOf(orderItem.getGiftIntegration()));
            // 购物获取的成长值
            growth = growth.add(BigDecimal.valueOf(orderItem.getGiftGrowth()));
        }

        // 总额、应付总额
        orderEntity.setTotalAmount(totalPrice);
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));

        // 优惠金额
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);

        // 积分、成长值
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
    }

    /**
     * 保存订单
     *
     * @param orderCreateTO 订单创建TO
     */
    private void saveOrder(OrderCreateTO orderCreateTO) {
        log.info("订单信息：" + orderCreateTO);

        // 保存订单数据
        final OrderEntity orderEntity = orderCreateTO.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        // 保存订单项数据
        List<OrderItemEntity> orderItems = orderCreateTO
                .getOrderItems()
                .stream()
                .peek(orderItemEntity -> {
                    orderItemEntity.setOrderId(orderEntity.getId());
                    orderItemEntity.setSpuName(orderItemEntity.getSkuName());
                    orderItemEntity.setOrderSn(orderEntity.getOrderSn());
                })
                .collect(Collectors.toList());
        orderItemService.saveBatch(orderItems);
    }

}
