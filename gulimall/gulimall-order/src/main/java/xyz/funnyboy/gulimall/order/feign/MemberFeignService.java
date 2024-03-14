package xyz.funnyboy.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.gulimall.order.vo.MemberAddressVO;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description 会员服务远程调用接口
 * @date 2024-03-11 16:30:11
 */
@FeignClient("gulimall-member")
public interface MemberFeignService
{
    @GetMapping("member/memberreceiveaddress/{memberId}/addresses")
    List<MemberAddressVO> getAddress(
            @PathVariable("memberId")
                    Long memberId);
}
