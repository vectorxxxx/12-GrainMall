package xyz.funnyboy.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-03 04:40:37
 */
@Service
@FeignClient(value = "gulimall-product")
public interface ProductFeignService
{
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(
            @PathVariable("skuId")
                    Long skuId);
}
