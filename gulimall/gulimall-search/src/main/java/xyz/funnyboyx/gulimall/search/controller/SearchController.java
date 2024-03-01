package xyz.funnyboyx.gulimall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.funnyboy.common.vo.search.SearchParam;
import xyz.funnyboy.common.vo.search.SearchResult;
import xyz.funnyboyx.gulimall.search.service.MallSearchService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-27 09:24:44
 */
@Controller
public class SearchController
{
    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());

        // 根据传递来的页面的查询参数，去es中检索商品
        final SearchResult result = mallSearchService.search(param);

        model.addAttribute("result", result);
        return "list";
    }
}
