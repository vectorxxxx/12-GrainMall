package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.product.entity.AttrGroupEntity;
import xyz.funnyboy.gulimall.product.vo.SpuItemAttrGroupVO;

import java.util.List;

/**
 * 属性分组
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity>
{

    /**
     * 通过 SPU ID 获取具有 ATTR  ATTR 组
     *
     * @param spuId     SPU ID
     * @param catalogId 目录 ID
     * @return {@link List}<{@link SpuItemAttrGroupVO}>
     */
    List<SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(
            @Param("spuId")
                    Long spuId,
            @Param("catalogId")
                    Long catalogId);
}
