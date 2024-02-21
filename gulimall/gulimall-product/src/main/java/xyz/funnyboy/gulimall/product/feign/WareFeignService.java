package xyz.funnyboy.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.funnyboy.common.utils.R;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-20 17:06:25
 */
@FeignClient("gulimall-ware")
public interface WareFeignService
{
    /**
     * 有库存
     *
     * @param skuIds SKU ID
     * @return {@link R}
     */
    @PostMapping("/ware/waresku/hasStock")
    R hasStock(
            @RequestBody
                    List<Long> skuIds);
}
