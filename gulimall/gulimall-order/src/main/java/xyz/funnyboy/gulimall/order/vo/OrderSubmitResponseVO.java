package xyz.funnyboy.gulimall.order.vo;

import lombok.Data;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;

/**
 * @author VectorX
 * @version V1.0
 * @description 下单返回数据接受类
 * @date 2024-03-12 14:41:29
 */
@Data
public class OrderSubmitResponseVO
{
    private OrderEntity orderEntity;

    /**
     * 错误状态码：0——成功
     */
    private Integer code;
}
