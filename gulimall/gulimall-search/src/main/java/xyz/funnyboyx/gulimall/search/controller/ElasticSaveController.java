package xyz.funnyboyx.gulimall.search.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.to.es.SkuEsModel;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboyx.gulimall.search.service.ProductSaveService;

import java.util.List;

/**
 * Es保存服务
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-21 10:37:24
 */
@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController
{
    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(
            @RequestBody
                    List<SkuEsModel> skuEsModelList) {
        boolean hasFailures;
        try {
            hasFailures = productSaveService.productStatusUp(skuEsModelList);
        }
        catch (Exception e) {
            log.error("ElasticSaveController商品上架错误: {}", e.getMessage(), e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (hasFailures) {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        return R.ok();
    }
}
