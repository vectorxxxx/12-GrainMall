package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.BrandDao;
import xyz.funnyboy.gulimall.product.dao.CategoryBrandRelationDao;
import xyz.funnyboy.gulimall.product.dao.CategoryDao;
import xyz.funnyboy.gulimall.product.entity.BrandEntity;
import xyz.funnyboy.gulimall.product.entity.CategoryBrandRelationEntity;
import xyz.funnyboy.gulimall.product.service.CategoryBrandRelationService;
import xyz.funnyboy.gulimall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService
{
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(new Query<CategoryBrandRelationEntity>().getPage(params), new QueryWrapper<CategoryBrandRelationEntity>());

        return new PageUtils(page);
    }

    /**
     * 保存详细信息
     *
     * @param categoryBrandRelation 品类品牌关系
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        // 查询品牌名称
        categoryBrandRelation.setBrandName(brandDao
                .selectById(categoryBrandRelation.getBrandId())
                .getName());
        // 查询分类名称
        categoryBrandRelation.setCatelogName(categoryDao
                .selectById(categoryBrandRelation.getCatelogId())
                .getName());
        // 保存
        this.save(categoryBrandRelation);
    }

    /**
     * 更新品牌名称
     *
     * @param brandId 品牌 ID
     * @param name    名字
     */
    @Override
    public void updateBrand(Long brandId, String name) {
        // 写法一
        // final List<CategoryBrandRelationEntity> list = baseMapper.selectList(
        //         new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getBrandId, brandId));
        // list.forEach(categoryBrandRelationEntity -> {
        //     categoryBrandRelationEntity.setBrandName(name);
        // });
        // this.updateBatchById(list);

        // 写法二
        final CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        this.update(categoryBrandRelationEntity, new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getBrandId, brandId));
    }

    @Override
    public List<BrandVo> getBrandByCatId(Long catId) {
        // 查询品牌 ID 列表
        final List<Long> brandIdList = baseMapper
                .selectList(new LambdaQueryWrapper<CategoryBrandRelationEntity>().eq(CategoryBrandRelationEntity::getCatelogId, catId))
                .stream()
                .map(CategoryBrandRelationEntity::getBrandId)
                .collect(Collectors.toList());

        // 查询品牌信息
        return brandDao
                .selectList(new LambdaQueryWrapper<BrandEntity>().in(BrandEntity::getBrandId, brandIdList))
                .stream()
                .map(brandEntity -> {
                    final BrandVo brandVo = new BrandVo();
                    brandVo.setBrandId(brandEntity.getBrandId());
                    brandVo.setBrandName(brandEntity.getName());
                    return brandVo;
                })
                .collect(Collectors.toList());
    }

}
