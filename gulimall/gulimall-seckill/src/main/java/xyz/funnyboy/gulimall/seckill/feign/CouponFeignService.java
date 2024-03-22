package xyz.funnyboy.gulimall.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-20 17:39:22
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService
{
    @GetMapping("/coupon/seckillsession/latest3DaySession")
    R getLatest3DaySession();
}
