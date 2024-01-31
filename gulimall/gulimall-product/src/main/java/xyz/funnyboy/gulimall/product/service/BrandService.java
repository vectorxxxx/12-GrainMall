package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface BrandService extends IService<BrandEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按 ID 详细信息更新
     *
     * @param brand 品牌
     */
    void updateByIdDetail(BrandEntity brand);
}

