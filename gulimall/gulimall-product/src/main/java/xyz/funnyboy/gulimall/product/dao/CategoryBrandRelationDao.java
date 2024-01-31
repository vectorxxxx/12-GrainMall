package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.product.entity.CategoryBrandRelationEntity;

/**
 * 品牌分类关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity>
{

    /**
     * 更新分类名称
     *
     * @param catelogId   分类 ID
     * @param catelogName 分类名称
     */
    void updateCategory(
            @Param("catelogId")
                    Long catelogId,
            @Param("catelogName")
                    String catelogName);
}
