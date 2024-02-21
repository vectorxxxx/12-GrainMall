package xyz.funnyboy.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.funnyboy.common.to.es.SkuEsModel;
import xyz.funnyboy.common.utils.R;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-20 17:41:23
 */
@FeignClient("gulimall-search")
public interface SearchFeignService
{
    @PostMapping("/search/save/product")
    R productStatusMap(
            @RequestBody
                    List<SkuEsModel> skuEsModel);
}
