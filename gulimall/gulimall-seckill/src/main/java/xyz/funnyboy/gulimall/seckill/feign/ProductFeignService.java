package xyz.funnyboy.gulimall.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.funnyboy.common.utils.R;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-21 09:45:53
 */
@FeignClient("gulimall-product")
public interface ProductFeignService
{
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(
            @PathVariable("skuId")
                    Long skuId);

    /**
     * 查询商品详情
     */
    @PostMapping("/product/skuinfo/infos")
    R getSkuInfos(
            @RequestBody
                    List<Long> skuIds);
}
