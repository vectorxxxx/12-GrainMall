package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SkuImagesService extends IService<SkuImagesEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按 SKU ID 获取图像
     *
     * @param skuId SKU ID
     * @return {@link List}<{@link SkuImagesEntity}>
     */
    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

