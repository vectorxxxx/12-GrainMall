package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.AttrAttrgroupRelationEntity;
import xyz.funnyboy.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量保存
     *
     * @param attrGroupRelations 属性-属性分组
     */
    void saveBatch(List<AttrGroupRelationVo> attrGroupRelations);
}

