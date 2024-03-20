package xyz.funnyboy.gulimall.order.vo;

import lombok.Data;

/**
 * @author VectorX
 * @version V1.0
 * @description 支付宝支付VO
 * @date 2024-03-19 16:27:54
 */
@Data
public class PayVO
{
    // 商户订单号 必填
    private String out_trade_no;
    // 订单名称 必填
    private String subject;
    // 付款金额 必填
    private String total_amount;
    // 商品描述 可空
    private String body;
}
