package xyz.funnyboy.gulimall.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.funnyboy.gulimall.order.service.OrderService;
import xyz.funnyboy.gulimall.order.vo.OrderConfirmVO;
import xyz.funnyboy.gulimall.order.vo.OrderSubmitResponseVO;
import xyz.funnyboy.gulimall.order.vo.OrderSubmitVO;

import java.util.concurrent.ExecutionException;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 14:11:08
 */
@Controller
public class OrderWebController
{
    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirmVO = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", orderConfirmVO);
        return "confirm";
    }

    @PostMapping("submitOrder")
    public String submitOrder(OrderSubmitVO orderSubmitVO, Model model, RedirectAttributes redirectAttributes) {
        // 下单: 创建订单，验令牌，验价格，锁库存...
        OrderSubmitResponseVO orderSubmitResponseVO = orderService.submitOrder(orderSubmitVO);

        // 下单成功来到支付选择页
        final Integer code = orderSubmitResponseVO.getCode();
        if (code == 0) {
            model.addAttribute("orderSubmitResponseVO", orderSubmitResponseVO);
            return "pay";
        }

        // 下单失败回到订单确认页重新确认订单信息
        String msg = "下单失败:";
        switch (code) {
            case 1:
                msg += "订单信息过期，请刷新后再次提交";
                break;
            case 2:
                msg += "订单价格发生变化，请确认后再次提交";
                break;
            case 3:
                msg += "库存锁定失败，商品库存不足";
                break;
            default:
                break;
        }
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:http://order.gulimall.com/toTrade";
    }
}
