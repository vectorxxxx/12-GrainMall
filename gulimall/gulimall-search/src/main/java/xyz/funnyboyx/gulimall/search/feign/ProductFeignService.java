package xyz.funnyboyx.gulimall.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.funnyboy.common.utils.R;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-29 18:36:12
 */
@FeignClient("gulimall-product")
public interface ProductFeignService
{
    @RequestMapping("/product/attr/info/{attrId}")
    R info(
            @PathVariable("attrId")
                    Long attrId);

    @RequestMapping("/product/brand/info/ids")
    R infoByIds(
            @RequestParam("brandIds")
                    List<Long> brandIds);
}
