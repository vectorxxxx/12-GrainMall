package xyz.funnyboy.gulimall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.constant.OrderConstant;
import xyz.funnyboy.gulimall.order.config.AliPayConfig;
import xyz.funnyboy.gulimall.order.entity.PaymentInfoEntity;
import xyz.funnyboy.gulimall.order.service.OrderService;
import xyz.funnyboy.gulimall.order.service.PayService;
import xyz.funnyboy.gulimall.order.vo.AliPayAsyncVO;
import xyz.funnyboy.gulimall.order.vo.PayVO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 17:12:01
 */
@Service
@Slf4j
public class AliPayServiceImpl implements PayService
{
    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private OrderService orderService;

    /**
     * 创建支付
     *
     * @param vo payVO
     */
    @Override
    public String pay(PayVO vo) throws AlipayApiException {
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
                // serverUrl
                aliPayConfig.getGatewayUrl(),
                // appId
                aliPayConfig.getApp_id(),
                // privateKey
                aliPayConfig.getMerchant_private_key(),
                // format,
                "json",
                // charset
                aliPayConfig.getCharset(),
                // alipayPublicKey
                aliPayConfig.getAlipay_public_key(),
                // signType
                aliPayConfig.getSign_type());

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(aliPayConfig.getReturn_url());
        alipayRequest.setNotifyUrl(aliPayConfig.getNotify_url());

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String outTradeNo = vo.getOut_trade_no();
        //付款金额，必填
        String totalAmount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        //构建请求参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("total_amount", totalAmount);
        bizContent.put("subject", subject);
        bizContent.put("body", body);
        bizContent.put("timeout_express", "1m");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(bizContent.toJSONString());

        String result = alipayClient
                .pageExecute(alipayRequest)
                .getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }

    @Override
    public void handlerPayResult(AliPayAsyncVO aliPayAsyncVO) {
        final String tradeFinished = aliPayAsyncVO.getTrade_status();
        log.info("支付宝支付成功状态：{}", tradeFinished);
        // 保存交易流水
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(aliPayAsyncVO.getOut_trade_no());//修改数据库为唯一属性
        paymentInfoEntity.setAlipayTradeNo(aliPayAsyncVO.getTrade_no());
        paymentInfoEntity.setTotalAmount(BigDecimal.valueOf(Double.parseDouble(aliPayAsyncVO.getBuyer_pay_amount())));
        paymentInfoEntity.setSubject(aliPayAsyncVO.getSubject());
        paymentInfoEntity.setPaymentStatus(tradeFinished);
        paymentInfoEntity.setCreateTime(new Date());
        paymentInfoEntity.setCallbackTime(aliPayAsyncVO.getNotify_time());

        // 修改订单状态
        Integer orderStatus = null;
        if ("TRADE_SUCCESS".equals(tradeFinished) || "TRADE_FINISHED".equals(tradeFinished)) {
            // 支付成功状态
            orderStatus = OrderConstant.OrderStatusEnum.PAYED.getCode();
        }
        orderService.handlePayResult(orderStatus, aliPayAsyncVO.getPayCode(), paymentInfoEntity);
    }

}
