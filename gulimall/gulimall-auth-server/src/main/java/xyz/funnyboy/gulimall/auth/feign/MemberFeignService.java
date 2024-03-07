package xyz.funnyboy.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.auth.vo.UserLoginVO;
import xyz.funnyboy.gulimall.auth.vo.UserRegVO;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 21:25:12
 */
@FeignClient("gulimall-member")
public interface MemberFeignService
{
    @PostMapping("/member/member/login")
    R login(
            @RequestBody
                    UserLoginVO vo);

    @PostMapping("/member/member/register")
    R register(
            @RequestBody
                    UserRegVO vo);
}
