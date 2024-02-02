package xyz.funnyboy.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
public interface WareInfoService extends IService<WareInfoEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按条件查询页面
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

