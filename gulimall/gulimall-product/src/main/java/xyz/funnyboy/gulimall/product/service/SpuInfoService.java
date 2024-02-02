package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SpuInfoEntity;
import xyz.funnyboy.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SpuInfoService extends IService<SpuInfoEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存 SPU 信息
     *
     * @param spuSaveVo SPU 保存 VO
     */
    void saveSpuInfo(SpuSaveVo spuSaveVo);

    /**
     * 按条件分页查询
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

