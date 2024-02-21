package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.product.entity.AttrEntity;

import java.util.List;

/**
 * 商品属性
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity>
{

    /**
     * 获取可以被用来检索的规格属性
     *
     * @param attrIds 属性ID
     * @return {@link List}<{@link Long}>
     */
    List<Long> selectSearchAttrIds(
            @Param("attrIds")
                    List<Long> attrIds);
}
