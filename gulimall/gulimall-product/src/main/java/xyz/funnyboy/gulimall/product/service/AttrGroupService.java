package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface AttrGroupService extends IService<AttrGroupEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询
     *
     * @param params    参数
     * @param catelogId 分类 ID
     * @return {@link PageUtils}
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

}

