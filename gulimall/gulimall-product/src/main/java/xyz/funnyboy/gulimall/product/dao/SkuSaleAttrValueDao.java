package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.product.entity.SkuSaleAttrValueEntity;
import xyz.funnyboy.gulimall.product.vo.SkuItemSaleAttrVO;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity>
{

    /**
     * 按 SPU ID 获取销售 Attrs
     *
     * @param spuId SPU ID
     * @return {@link List}<{@link SkuItemSaleAttrVO}>
     */
    List<SkuItemSaleAttrVO> getSaleAttrsBySpuId(
            @Param("spuId")
                    Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}
