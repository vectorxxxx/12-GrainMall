package xyz.funnyboy.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.funnyboy.gulimall.order.vo.OrderItemVO;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description 购物车服务远程调用接口
 * @date 2024-03-11 16:35:15
 */
@FeignClient("gulimall-cart")
public interface CartFeignService
{
    @GetMapping("currentUserCartItems")
    List<OrderItemVO> getCurrentUserCartItems();
}
