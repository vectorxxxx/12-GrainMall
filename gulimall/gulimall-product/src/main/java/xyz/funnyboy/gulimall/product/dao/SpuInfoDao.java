package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.product.entity.SpuInfoEntity;

/**
 * spu信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity>
{

    /**
     * 更新 SPU 状态
     *
     * @param spuId SPU ID
     * @param code  代码
     */
    void updateSpuStatus(
            @Param("spuId")
                    Long spuId,
            @Param("code")
                    int code);
}
