package xyz.funnyboy.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.CategoryBrandRelationDao;
import xyz.funnyboy.gulimall.product.dao.CategoryDao;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.CategoryService;
import xyz.funnyboy.gulimall.product.vo.Catalog2VO;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService
{
    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        // 1、缓存穿透（查询结果为空）：空结果缓存
        // 2、缓存雪崩（相同过期时间）：设置过期时间（加随机值）
        // 3、缓存击穿（热点key失效）：加锁
        
        // 从缓存中查询
        final String catalogJson = stringRedisTemplate
                .opsForValue()
                .get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            log.info("缓存命中...直接返回...");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2VO>>>() {});
        }

        log.info("缓存未命中...即将查询数据库...");
        // 缓存中没有在从数据库中查询
        return getCatalogJsonFromDb();
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDb() {
        synchronized (this) {
            final String catalogJson = stringRedisTemplate
                    .opsForValue()
                    .get("catalogJson");
            if (!StringUtils.isEmpty(catalogJson)) {
                log.info("缓存命中...直接返回...");
                return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2VO>>>() {});
            }

            log.info("缓存未命中...查询数据库...");
            // 查询所有分类，并按照父 ID 分组
            final Map<Long, List<CategoryEntity>> categoryMap = baseMapper
                    .selectList(null)
                    .stream()
                    .collect(Collectors.groupingBy(CategoryEntity::getParentCid));
            // 查询一级分类
            final Map<String, List<Catalog2VO>> catalogJsonFromDb = categoryMap
                    .get(0L)
                    .stream()
                    .collect(Collectors.toMap(l1 -> l1
                            .getCatId()
                            .toString(), l1 -> categoryMap
                            .get(l1.getCatId())
                            .stream()
                            .map(l2 -> {
                                final List<Catalog2VO.Catalog3VO> catalog3VOList = categoryMap
                                        .get(l2.getCatId())
                                        .stream()
                                        .map(l3 -> new Catalog2VO.Catalog3VO(l2
                                                .getCatId()
                                                .toString(), l3
                                                .getCatId()
                                                .toString(), l3.getName()))
                                        .collect(Collectors.toList());
                                return new Catalog2VO(l1
                                        .getCatId()
                                        .toString(), catalog3VOList, l2
                                        .getCatId()
                                        .toString(), l2.getName());
                            })
                            .collect(Collectors.toList())));

            // 存入缓存
            stringRedisTemplate
                    .opsForValue()
                    .set("catalogJson", JSON.toJSONString(catalogJsonFromDb), 1, TimeUnit.DAYS);

            // 返回结果
            return catalogJsonFromDb;
        }
    }

    /**
     * 获取 catelog json
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catalog2VO}>>
     */
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJsonBeforeOptimization() {
        // 查询所有分类，并按照父 ID 分组
        // 查询一级分类
        return getLevel1Categorys()
                .stream()
                .collect(Collectors.toMap(l1 -> l1
                        .getCatId()
                        .toString(), l1 -> baseMapper
                        .selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, l1.getCatId()))
                        .stream()
                        .map(l2 -> {
                            final List<Catalog2VO.Catalog3VO> catalog3VOList = baseMapper
                                    .selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, l2.getCatId()))
                                    .stream()
                                    .map(l3 -> new Catalog2VO.Catalog3VO(l2
                                            .getCatId()
                                            .toString(), l3
                                            .getCatId()
                                            .toString(), l3.getName()))
                                    .collect(Collectors.toList());
                            return new Catalog2VO(l1
                                    .getCatId()
                                    .toString(), catalog3VOList, l2
                                    .getCatId()
                                    .toString(), l2.getName());
                        })
                        .collect(Collectors.toList())));
    }
}
