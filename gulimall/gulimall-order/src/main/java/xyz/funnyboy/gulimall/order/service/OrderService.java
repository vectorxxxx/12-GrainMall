package xyz.funnyboy.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;
import xyz.funnyboy.gulimall.order.vo.OrderConfirmVO;
import xyz.funnyboy.gulimall.order.vo.OrderSubmitResponseVO;
import xyz.funnyboy.gulimall.order.vo.OrderSubmitVO;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:19:36
 */
public interface OrderService extends IService<OrderEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 确认订单
     *
     * @return {@link OrderConfirmVO}
     */
    OrderConfirmVO confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 提交订单
     *
     * @param orderSubmitVO 订单提交 VO
     * @return {@link OrderSubmitResponseVO}
     */
    OrderSubmitResponseVO submitOrder(OrderSubmitVO orderSubmitVO);

    /**
     * 按orderSn获取订单
     *
     * @param orderSn orderSn
     * @return {@link OrderEntity}
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭订单
     *
     * @param entity 实体
     */
    void closeOrder(OrderEntity entity);
}

