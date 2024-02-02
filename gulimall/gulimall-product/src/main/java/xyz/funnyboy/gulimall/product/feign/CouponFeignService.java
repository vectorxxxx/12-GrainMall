package xyz.funnyboy.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.funnyboy.common.to.SkuReductionTO;
import xyz.funnyboy.common.to.SpuBoundTO;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-02 11:40:40
 */
@Component
@FeignClient("gulimall-coupon")
public interface CouponFeignService
{
    /**
     * 保存 SPU 积分信息
     *
     * @param spuBoundTO SPU 积分 TO
     * @return {@link R}
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(
            @RequestBody
                    SpuBoundTO spuBoundTO);

    /**
     * 保存 SKU 优惠信息
     *
     * @param skuReductionTO SKU 优惠 TO
     * @return {@link R}
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(
            @RequestBody
                    SkuReductionTO skuReductionTO);
}
