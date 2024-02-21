package xyz.funnyboy.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;

/**
 * 商品库存
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity>
{

    /**
     * 获取 SKU 库存
     *
     * @param skuId SKU ID
     * @return {@link Long}
     */
    Long getSkuStock(
            @Param("skuId")
                    Long skuId);
}
