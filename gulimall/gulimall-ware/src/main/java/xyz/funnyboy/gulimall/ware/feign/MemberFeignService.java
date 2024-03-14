package xyz.funnyboy.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-12 10:40:30
 */
@FeignClient("gulimall-member")
public interface MemberFeignService
{
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(
            @PathVariable("id")
                    Long id);
}
