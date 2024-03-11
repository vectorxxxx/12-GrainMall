package xyz.funnyboy.gulimall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 14:11:08
 */
@Controller
public class OrderWebController
{
    @GetMapping("/toTrade")
    public String toTrade() {
        return "confirm";
    }
}
