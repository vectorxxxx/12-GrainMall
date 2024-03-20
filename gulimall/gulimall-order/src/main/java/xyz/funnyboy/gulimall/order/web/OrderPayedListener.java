package xyz.funnyboy.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.gulimall.order.config.AliPayConfig;
import xyz.funnyboy.gulimall.order.service.impl.AliPayServiceImpl;
import xyz.funnyboy.gulimall.order.vo.AliPayAsyncVO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-20 09:42:44
 */
@Slf4j
@RestController
public class OrderPayedListener
{
    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private AliPayServiceImpl aliPayService;

    @PostMapping("/payed/notify")
    public String handlerAlipay(HttpServletRequest request, AliPayAsyncVO aliPayAsyncVO) throws AlipayApiException {
        log.info("收到支付宝异步通知******************");

        // 只要收到支付宝的异步通知，返回 success 支付宝便不再通知
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            // 乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, String.join(",", values));
        }

        // 调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipay_public_key(), aliPayConfig.getCharset(), aliPayConfig.getSign_type());
        if (!signVerified) {
            log.error("支付宝异步通知验签失败");
            return "error";
        }
        log.info("支付宝异步通知验签成功");

        // 修改订单状态
        aliPayService.handlerPayResult(aliPayAsyncVO);
        return "success";
    }
}
