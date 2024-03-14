package xyz.funnyboy.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.order.vo.WareSkuLockVO;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description 远程调用库存系统
 * @date 2024-03-12 09:34:41
 */
@FeignClient("gulimall-ware")
public interface WmsFeignService
{   //查询sku是否有库存
    @PostMapping("/ware/waresku/hasStock")
    R hasStock(
            @RequestBody
                    List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    R getFare(
            @RequestParam("addrId")
                    Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(
            @RequestBody
                    WareSkuLockVO vo);
}
