package xyz.funnyboy.gulimall.member.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.member.feign.OrderFeignService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 16:55:37
 */
@Controller
public class MemberWebController
{
    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(
            @RequestParam(value = "pageNum",
                          defaultValue = "1")
                    Integer pageNum, Model model) {
        final Map<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());
        final R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders", r);
        return "orderList";
    }
}
