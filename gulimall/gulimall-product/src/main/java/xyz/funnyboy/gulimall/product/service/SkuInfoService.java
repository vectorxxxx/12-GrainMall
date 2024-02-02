package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SkuInfoService extends IService<SkuInfoEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按条件分页查询
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

