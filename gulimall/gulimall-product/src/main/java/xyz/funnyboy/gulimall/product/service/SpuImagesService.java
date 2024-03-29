package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SpuImagesService extends IService<SpuImagesEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存图片集合
     *
     * @param images 图像
     * @param spuId  SPU ID
     */
    void saveImages(Long spuId, List<String> images);
}

