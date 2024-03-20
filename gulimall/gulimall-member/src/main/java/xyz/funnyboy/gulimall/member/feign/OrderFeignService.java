package xyz.funnyboy.gulimall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.funnyboy.common.utils.R;

import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 17:41:30
 */
@FeignClient("gulimall-order")
public interface OrderFeignService
{
    @PostMapping("/order/order/listWithItem")
    R listWithItem(
            @RequestBody
                    Map<String, Object> params);
}
