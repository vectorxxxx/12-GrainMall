package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.product.entity.AttrAttrgroupRelationEntity;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity>
{

    /**
     * 删除关联关系
     *
     * @param entities ATTR ATTRGROUP Relation 实体列表
     */
    void deleteBatchRelation(
            @Param("entities")
                    List<AttrAttrgroupRelationEntity> entities);
}
