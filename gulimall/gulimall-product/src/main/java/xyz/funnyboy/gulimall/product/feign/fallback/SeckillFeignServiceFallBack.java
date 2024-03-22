package xyz.funnyboy.gulimall.product.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.feign.SeckillFeignService;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-22 16:13:47
 */
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService
{
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.error("熔断方法调用...getSkuSeckillInfo");
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
    }
}
