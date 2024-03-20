package xyz.funnyboy.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Set;

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

    /**
     * 列出有库存的仓库ID
     *
     * @param skuId SKU ID
     * @return {@link List}<{@link Long}>
     */
    List<Long> listWareIdHasSkuStock(
            @Param("skuId")
                    Long skuId);

    /**
     * 锁定商品库存
     *
     * @param skuId  SKU ID
     * @param wareId Ware ID
     * @param num    数量
     * @return {@link Long}
     */
    Long lockSkuStock(
            @Param("skuId")
                    Long skuId,
            @Param("wareId")
                    Long wareId,
            @Param("num")
                    Integer num);

    /**
     * 解锁 SKU 库存
     *
     * @param skuId  SKU ID
     * @param wareId Ware ID
     * @param skuNum SKU 数量
     */
    void unlockSkuStock(
            @Param("skuId")
                    Long skuId,
            @Param("wareId")
                    Long wareId,
            @Param("num")
                    Integer skuNum);

    /**
     * 查询商品库存充足的仓库
     *
     * @param skuIds SKU ID
     * @return {@link List}<{@link WareSkuEntity}>
     */
    List<WareSkuEntity> selectListHasSkuStock(
            @Param("skuIds")
                    Set<Long> skuIds);
}
