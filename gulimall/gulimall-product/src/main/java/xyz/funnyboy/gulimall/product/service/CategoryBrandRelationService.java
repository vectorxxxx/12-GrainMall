package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存详细信息
     *
     * @param categoryBrandRelation 品类品牌关系
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新品牌名称
     *
     * @param brandId 品牌 ID
     * @param name    名字
     */
    void updateBrand(Long brandId, String name);
}

