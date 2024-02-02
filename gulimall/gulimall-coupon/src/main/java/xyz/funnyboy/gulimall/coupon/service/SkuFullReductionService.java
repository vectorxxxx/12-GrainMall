package xyz.funnyboy.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.to.SkuReductionTO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:00:18
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存 SKU 优惠信息
     *
     * @param skuReductionTO SKU 减少到
     */
    void saveSkuReduction(SkuReductionTO skuReductionTO);
}

