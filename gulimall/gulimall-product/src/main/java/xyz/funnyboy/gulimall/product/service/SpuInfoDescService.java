package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SpuInfoDescEntity;

import java.util.List;
import java.util.Map;

/**
 * spu信息介绍
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存spu的描述图片
     *
     * @param descript 描述
     * @param spuId    SPU ID
     */
    void saveDescript(Long spuId, List<String> descript);
}

