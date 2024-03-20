package xyz.funnyboy.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.gulimall.order.service.OrderService;
import xyz.funnyboy.gulimall.order.service.PayService;
import xyz.funnyboy.gulimall.order.vo.PayVO;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 16:33:38
 */
@Slf4j
@Controller
public class PayWebController
{
    @Autowired
    private PayService payService;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder",
                produces = "text/html")
    public String payOrder(
            @RequestParam("orderSn")
                    String orderSn) throws AlipayApiException {
        PayVO payVO = orderService.getOrderPay(orderSn);
        final String pay = payService.pay(payVO);
        log.info("支付结果:{}", pay);
        return pay;
    }
}
