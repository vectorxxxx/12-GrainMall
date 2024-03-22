package xyz.funnyboy.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-21 19:58:51
 */
@FeignClient("gulimall-seckill")
public interface SeckillFeignService
{
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(
            @PathVariable("skuId")
                    Long skuId);
}
