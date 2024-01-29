package xyz.funnyboy.gulimall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-29 14:29:22
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService
{
    @RequestMapping("/coupon/coupon/member/list")
    R membercoupons();
}
