package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.AttrEntity;
import xyz.funnyboy.gulimall.product.vo.AttrRespVo;
import xyz.funnyboy.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface AttrService extends IService<AttrEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存
     *
     * @param attr ATTR
     */
    void saveAttr(AttrVo attr);

    /**
     * 分页查询基本属性
     *
     * @param params    参数
     * @param catelogId 分类 ID
     * @param attrType  属性 类型
     * @return {@link PageUtils}
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    /**
     * 获取 属性 信息
     *
     * @param attrId 属性 ID
     * @return {@link AttrRespVo}
     */
    AttrRespVo getAttrInfo(Long attrId);

    /**
     * 更新 属性
     *
     * @param attr 属性
     */
    void updateAttr(AttrVo attr);

    /**
     * 获取有关联关系的属性
     *
     * @param attrgroupId attrgroup ID
     * @return {@link List}<{@link AttrEntity}>
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 获取无关联关系的属性
     *
     * @param params      参数
     * @param attrgroupId attrgroup ID
     * @return {@link PageUtils}
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

