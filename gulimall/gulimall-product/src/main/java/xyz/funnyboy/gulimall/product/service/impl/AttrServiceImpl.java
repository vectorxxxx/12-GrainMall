package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.constant.ProductConstant;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.AttrAttrgroupRelationDao;
import xyz.funnyboy.gulimall.product.dao.AttrDao;
import xyz.funnyboy.gulimall.product.dao.AttrGroupDao;
import xyz.funnyboy.gulimall.product.dao.CategoryDao;
import xyz.funnyboy.gulimall.product.entity.AttrAttrgroupRelationEntity;
import xyz.funnyboy.gulimall.product.entity.AttrEntity;
import xyz.funnyboy.gulimall.product.entity.AttrGroupEntity;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.AttrService;
import xyz.funnyboy.gulimall.product.service.CategoryService;
import xyz.funnyboy.gulimall.product.vo.AttrRespVo;
import xyz.funnyboy.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService
{
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), new QueryWrapper<AttrEntity>());

        return new PageUtils(page);
    }

    /**
     * 保存
     *
     * @param attr ATTR
     */
    @Override
    public void saveAttr(AttrVo attr) {
        // 保存规格参数基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);

        // 保存规格参数级联信息
        if (attr.getAttrGroupId() != null && attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrSort(0);
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    /**
     * 分页查询基本属性
     *
     * @param params    参数
     * @param catelogId 分类 ID
     * @param attrType  属性 类型
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        // 分页条件
        final IPage<AttrEntity> pageParam = new Query<AttrEntity>().getPage(params);
        // 查询条件
        final String key = (String) params.get("key");
        final LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<AttrEntity>()
                .eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(attrType) ?
                                             ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                                             ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode())
                .eq(catelogId != null && catelogId != 0, AttrEntity::getCatelogId, catelogId)
                .and(!StringUtils.isEmpty(key), w -> w
                        .eq(AttrEntity::getAttrId, key)
                        .or()
                        .like(AttrEntity::getAttrName, key));
        // 分页查询
        IPage<AttrEntity> page = this.page(pageParam, queryWrapper);
        final PageUtils pageUtils = new PageUtils(page);

        // 组装分类名称、属性分组名称
        final List<AttrRespVo> attrRespVoList = page
                .getRecords()
                .stream()
                .map(item -> {
                    final AttrRespVo attrRespVo = new AttrRespVo();
                    BeanUtils.copyProperties(item, attrRespVo);

                    // 分类名称
                    final CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
                    if (categoryEntity != null) {
                        attrRespVo.setCatelogName(categoryEntity.getName());
                    }

                    // 属性分组名称
                    if ("base".equalsIgnoreCase(attrType)) {
                        final AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                                new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId()));
                        if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                            final AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                            if (attrGroupEntity != null) {
                                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                            }
                        }
                    }

                    return attrRespVo;
                })
                .collect(Collectors.toList());
        pageUtils.setList(attrRespVoList);

        return pageUtils;
    }

    /**
     * 获取 属性 信息
     *
     * @param attrId 属性 ID
     * @return {@link AttrRespVo}
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        // 查询属性
        final AttrEntity attrEntity = this.getById(attrId);

        // 组装属性
        final AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        // 分类名称 / 分类名称路径
        final Long catelogId = attrEntity.getCatelogId();
        final CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
            attrRespVo.setCatelogPath(categoryService.findCatelogPath(catelogId));
        }

        // 属性分组名称
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            final AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    final AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        return attrRespVo;
    }

    /**
     * 更新 属性
     *
     * @param attr 属性
     */
    @Override
    public void updateAttr(AttrVo attr) {
        // 更新属性基本信息
        final AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        // 更新 属性-属性分组 关联信息
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            final AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            final LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId,
                    attr.getAttrId());
            final Integer count = attrAttrgroupRelationDao.selectCount(queryWrapper);
            if (count > 0) {
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, queryWrapper);
            }
            else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        final List<Long> attrIdList = attrAttrgroupRelationDao
                // 查询属性分组列表
                .selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId))
                .stream()
                // 转换为属性ID列表
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIdList)) {
            return null;
        }

        // 查询属性列表
        return baseMapper.selectBatchIds(attrIdList);
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 根据属性分组ID查询当前分类ID
        final Long catelogId = attrGroupDao
                .selectOne(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getAttrGroupId, attrgroupId))
                .getCatelogId();

        // 根据分类ID查询当前分类下的所有属性分组ID
        final List<Long> attrGroupIdList = attrGroupDao
                .selectList(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, catelogId))
                .stream()
                .map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList());

        // 根据属性分组ID查询关联关系中的属性ID
        final List<Long> attrIdList = attrAttrgroupRelationDao
                .selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIdList))
                .stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());

        // 根据属性ID查询所有属性，排除属性分组ID列表中的属性
        final String key = (String) params.get("key");
        final LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<AttrEntity>()
                .eq(AttrEntity::getCatelogId, catelogId)
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())
                .notIn(!CollectionUtils.isEmpty(attrIdList), AttrEntity::getAttrId, attrIdList)
                .and(!StringUtils.isEmpty(key), w -> w
                        .eq(AttrEntity::getAttrId, key)
                        .or()
                        .like(AttrEntity::getAttrName, key));
        final IPage<AttrEntity> pageParam = new Query<AttrEntity>().getPage(params);
        final IPage<AttrEntity> page = baseMapper.selectPage(pageParam, queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }

}
