package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SkuSaleAttrValueEntity;
import xyz.funnyboy.gulimall.product.vo.SkuItemSaleAttrVO;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按 SPU ID 获取销售 Attrs
     *
     * @param spuId SPU ID
     * @return {@link List}<{@link SkuItemSaleAttrVO}>
     */
    List<SkuItemSaleAttrVO> getSaleAttrsBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

