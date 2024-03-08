package xyz.funnyboy.gulimall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.funnyboy.common.utils.R;

import java.util.List;

/**
 * 商品服务远程接口
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 10:53:52
 */
@FeignClient("gulimall-product")
public interface ProductFeignService
{
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(
            @PathVariable("skuId")
                    Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValues(
            @PathVariable("skuId")
                    Long skuId);
}
