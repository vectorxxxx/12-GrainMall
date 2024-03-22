package xyz.funnyboy.gulimall.seckill.service;

import xyz.funnyboy.common.to.seckill.SeckillSkuRedisTO;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-20 18:04:17
 */
public interface SeckillService
{
    /**
     * 上架最近三天需要秒杀的商品
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 获取当前秒杀的商品
     *
     * @return {@link List}<{@link SeckillSkuRedisTO}>
     */
    List<SeckillSkuRedisTO> getCurrentSeckillSkus();

    /**
     * 根据skuId查询商品当前时间秒杀信息
     *
     * @param skuId SKU ID
     * @return {@link SeckillSkuRedisTO}
     */
    SeckillSkuRedisTO getSkuSeckilInfo(Long skuId);

    /**
     * 秒杀商品
     * <p>
     * 1.校验登录状态
     * <p>
     * 2.校验秒杀时间
     * <p>
     * 3.校验随机码、场次、商品对应关系
     * <p>
     * 4.校验信号量扣减，校验购物数量是否限购
     * <p>
     * 5.校验是否重复秒杀（幂等性）【秒杀成功SETNX占位  userId_sessionId_skuId】
     * <p>
     * 6.扣减信号量
     * <p>
     * 7.发送消息，创建订单号和订单信息
     * <p>
     * 8.订单模块消费消息，生成订单
     *
     * @param killId 杀戮 ID
     * @param key    钥匙
     * @param num    数字
     * @return {@link String}
     */
    String kill(String killId, String key, Integer num) throws InterruptedException;
}
