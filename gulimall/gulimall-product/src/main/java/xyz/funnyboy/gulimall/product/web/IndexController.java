package xyz.funnyboy.gulimall.product.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.CategoryService;
import xyz.funnyboy.gulimall.product.vo.Catalog2VO;

import java.util.List;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-22 22:43:56
 */
@Controller
public class IndexController
{
    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        final List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatlogJson() {
        return categoryService.getCatelogJson();
    }
}
