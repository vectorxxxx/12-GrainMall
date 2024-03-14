package xyz.funnyboy.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author VectorX
 * @version V1.0
 * @description 封装订单提交的数据
 * @date 2024-03-12 14:27:34
 */
@Data
public class OrderSubmitVO
{
    /**
     * 地址 ID
     */
    private Long addrId;

    /**
     * 支付类型
     */
    private Integer payType;

    /**
     * 防重令牌
     */
    private String orderToken;

    /**
     * 应付价格
     */
    private BigDecimal payPrice;

    /**
     * 备注
     */
    private String note;
}
