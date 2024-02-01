package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.AttrAttrgroupRelationDao;
import xyz.funnyboy.gulimall.product.dao.AttrGroupDao;
import xyz.funnyboy.gulimall.product.entity.AttrAttrgroupRelationEntity;
import xyz.funnyboy.gulimall.product.entity.AttrGroupEntity;
import xyz.funnyboy.gulimall.product.service.AttrGroupService;
import xyz.funnyboy.gulimall.product.service.CategoryService;
import xyz.funnyboy.gulimall.product.vo.AttrGroupRelationVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService
{
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), new QueryWrapper<AttrGroupEntity>());

        return new PageUtils(page);
    }

    /**
     * 分页查询
     *
     * @param params    参数
     * @param catelogId 分类 ID
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        // 分页参数
        final IPage<AttrGroupEntity> pageParam = new Query<AttrGroupEntity>().getPage(params);
        // 查询条件
        final String key = (String) params.get("key");
        final LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(catelogId != null && catelogId != 0, AttrGroupEntity::getCatelogId, catelogId)
                .and(!StringUtils.isEmpty(key), w -> w
                        .eq(AttrGroupEntity::getAttrGroupId, key)
                        .or()
                        .like(AttrGroupEntity::getAttrGroupName, key));

        // 分页查询
        IPage<AttrGroupEntity> page = this.page(pageParam, queryWrapper);

        // 获取当前分类的完整路径
        page
                .getRecords()
                .forEach(attrGroup -> {
                    final String catelogPath = categoryService.findCatelogPathName(attrGroup.getCatelogId());
                    attrGroup.setCatelogPathName(catelogPath);
                });

        return new PageUtils(page);
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] attrGroupEntities) {
        // 转换成持久化对象
        final List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = Arrays
                .stream(attrGroupEntities)
                .map(attrGroupEntity -> {
                    final AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(attrGroupEntity, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                })
                .collect(Collectors.toList());
        // 批量删除关联关系
        attrAttrgroupRelationDao.deleteBatchRelation(attrAttrgroupRelationEntityList);
    }
}
