package xyz.funnyboy.gulimall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 10:50:48
 */
@Controller
public class HelloController
{
    @GetMapping("/{page}.html")
    public String hello(
            @PathVariable("page")
                    String page) {
        return page;
    }
}
