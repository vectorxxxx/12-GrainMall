package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.CategoryBrandRelationDao;
import xyz.funnyboy.gulimall.product.dao.CategoryDao;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.CategoryService;
import xyz.funnyboy.gulimall.product.vo.Catelog2VO;

import java.util.*;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService
{
    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(new Query<CategoryEntity>().getPage(params), new QueryWrapper<CategoryEntity>());

        return new PageUtils(page);
    }

    /**
     * 查出所有分类 以及子分类，以树形结构组装起来
     *
     * @return {@link List}<{@link CategoryEntity}>
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        final List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);
        return categoryEntityList
                .stream()
                .filter(menu -> menu.getParentCid() == 0)
                .peek(menu -> menu.setChildren(this.getChildren(menu, categoryEntityList)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ?
                                                         0 :
                                                         menu.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * 按 ID 删除菜单
     *
     * @param ids IDS
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    private List<CategoryEntity> getChildren(CategoryEntity parent, List<CategoryEntity> categoryEntityList) {
        return categoryEntityList
                .stream()
                .filter(menu -> menu
                        .getParentCid()
                        .longValue() == parent
                        .getCatId()
                        .longValue())
                .peek(menu -> menu.setChildren(this.getChildren(menu, categoryEntityList)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ?
                                                         0 :
                                                         menu.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * 查找 Catelog 路径
     *
     * @param catelogId 分类 ID
     * @return {@link Long[]}
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> catelogPathList = new ArrayList<>();
        this.findCatelogPath(catelogPathList, catelogId);
        Collections.reverse(catelogPathList);
        return catelogPathList.toArray(new Long[0]);
    }

    /**
     * 递归查找 Catelog 路径
     *
     * @param catelogPathList 分类路径列表
     * @param catelogId       分类 ID
     */
    private void findCatelogPath(List<Long> catelogPathList, Long catelogId) {
        catelogPathList.add(catelogId);

        final CategoryEntity category = baseMapper.selectById(catelogId);
        if (category == null) {
            return;
        }

        final Long parentCid = category.getParentCid();
        if (catelogId == null || parentCid == 0) {
            return;
        }

        findCatelogPath(catelogPathList, parentCid);
    }

    /**
     * 查找 Catelog 路径名
     *
     * @param catelogId 分类 ID
     * @return {@link String}
     */
    @Override
    public String findCatelogPathName(Long catelogId) {
        List<String> catelogPathNameList = new ArrayList<>();
        this.findCatelogPathName(catelogPathNameList, catelogId);
        Collections.reverse(catelogPathNameList);
        return String.join(" / ", catelogPathNameList);
    }

    private void findCatelogPathName(List<String> catelogPathNameList, Long catelogId) {
        final CategoryEntity category = this.getById(catelogId);
        if (category == null) {
            return;
        }
        catelogPathNameList.add(category.getName());

        final Long parentCid = category.getParentCid();
        if (parentCid == null || parentCid == 0) {
            return;
        }
        findCatelogPathName(catelogPathNameList, parentCid);
    }

    /**
     * 级联更新数据
     *
     * @param category 类别
     */
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationDao.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
    }

    /**
     * 获取 catelog json
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catelog2VO}>>
     */
    @Cacheable(value = "category",
               key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2VO>> getCatelogJson() {
        // 查询所有分类，并按照父 ID 分组
        final Map<Long, List<CategoryEntity>> categoryMap = baseMapper
                .selectList(null)
                .stream()
                .collect(Collectors.groupingBy(CategoryEntity::getParentCid));
        // 查询一级分类
        return categoryMap
                .get(0L)
                .stream()
                .collect(Collectors.toMap(k -> k
                        .getCatId()
                        .toString(), l1 -> categoryMap
                        .get(l1.getCatId())
                        .stream()
                        .map(l2 -> new Catelog2VO(l2
                                .getCatId()
                                .toString(), l2.getName(), l1
                                .getCatId()
                                .toString(), categoryMap
                                .get(l2.getCatId())
                                .stream()
                                .map(l3 -> new Catelog2VO.Catelog3VO(l3
                                        .getCatId()
                                        .toString(), l3.getName(), l2
                                        .getCatId()
                                        .toString()))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toList())));
    }
}
