package xyz.funnyboy.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @description 订单服务的远程调用
 * @date 2024-03-19 10:44:57
 */
@FeignClient("gulimall-order")
public interface OrderFeignService
{
    @GetMapping("/order/order/status/{orderSn}")
    R getOrderStatus(
            @PathVariable("orderSn")
                    String orderSn);
}
