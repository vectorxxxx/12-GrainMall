package xyz.funnyboy.gulimall.order.to;

import lombok.Data;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;
import xyz.funnyboy.gulimall.order.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-12 14:47:14
 */
@Data
public class OrderCreateTO
{
    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    /**
     * 应付价格
     */
    private BigDecimal payPrice;

    /**
     * 运费
     */
    private BigDecimal fare;
}
