package xyz.funnyboy.gulimall.order.service;

import com.alipay.api.AlipayApiException;
import xyz.funnyboy.gulimall.order.vo.AliPayAsyncVO;
import xyz.funnyboy.gulimall.order.vo.PayVO;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 17:11:05
 */
public interface PayService
{
    /**
     * 创建支付
     *
     * @param vo payVO
     */
    String pay(PayVO vo) throws AlipayApiException;

    /**
     * 处理支付结果
     *
     * @param aliPayAsyncVO aliPayAsyncVO
     */
    void handlerPayResult(AliPayAsyncVO aliPayAsyncVO);
}
