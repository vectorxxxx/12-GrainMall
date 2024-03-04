package xyz.funnyboy.gulimall.product.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.gulimall.product.service.SkuInfoService;
import xyz.funnyboy.gulimall.product.vo.SkuItemVO;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-04 22:24:02
 */
@Controller
@Slf4j
public class ItemController
{
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(
            @PathVariable("skuId")
                    Long skuId, Model model) {
        System.out.println("准备查询：" + skuId + "的详情");
        SkuItemVO skuItemVO = skuInfoService.item(skuId);
        model.addAttribute("item", skuItemVO);
        return "item";
    }
}
