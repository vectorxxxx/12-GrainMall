package xyz.funnyboyx.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-27 09:24:44
 */
@Controller
public class SearchController
{
    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }
}
