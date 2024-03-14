package xyz.funnyboy.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @description 商品服务远程调用接口
 * @date 2024-03-12 14:54:07
 */
@FeignClient("gulimall-product")
public interface ProductFeignService
{
    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(
            @PathVariable("id")
                    Long skuId);
}
