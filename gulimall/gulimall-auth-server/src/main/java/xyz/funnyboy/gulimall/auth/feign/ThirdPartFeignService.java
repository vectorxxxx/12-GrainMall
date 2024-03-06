package xyz.funnyboy.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 19:28:42
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService
{
    @GetMapping("/sms/sendCode")
    R sendCode(
            @RequestParam("phone")
                    String phone,
            @RequestParam("code")
                    String code);
}
