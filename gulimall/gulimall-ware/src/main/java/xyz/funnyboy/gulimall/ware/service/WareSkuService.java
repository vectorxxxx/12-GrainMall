package xyz.funnyboy.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.to.es.SkuHasStockVO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
public interface WareSkuService extends IService<WareSkuEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 添加库存
     *
     * @param wareId 软件 ID
     * @param skuId  SKU 编号
     * @param skuNum SKU 编号
     * @return double
     */
    double addStock(Long wareId, Long skuId, Integer skuNum);

    /**
     * 获取SkuHasStock
     *
     * @param skuIds SKU ID
     * @return {@link List}<{@link SkuHasStockVO}>
     */
    List<SkuHasStockVO> getSkuHasStock(List<Long> skuIds);
}

