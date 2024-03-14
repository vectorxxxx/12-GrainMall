package xyz.funnyboy.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description 接收库存锁定相关数据
 * @date 2024-03-12 14:56:37
 */
@Data
public class WareSkuLockVO
{
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 要锁住的所有库存信息
     */
    private List<OrderItemVO> locks;
}
