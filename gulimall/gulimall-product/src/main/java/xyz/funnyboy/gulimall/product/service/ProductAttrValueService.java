package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.ProductAttrValueEntity;
import xyz.funnyboy.gulimall.product.vo.BaseAttrs;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存基本属性
     *
     * @param spuId     SPU ID
     * @param baseAttrs 基础属性
     */
    void saveBaseAttrs(Long spuId, List<BaseAttrs> baseAttrs);

    /**
     * 获取spu规格
     *
     * @param spuId SPU ID
     * @return {@link List}<{@link ProductAttrValueEntity}>
     */
    List<ProductAttrValueEntity> baseAttrlistForSpu(Long spuId);

    /**
     * 修改商品规格
     *
     * @param spuId    SPU ID
     * @param entities 实体
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

