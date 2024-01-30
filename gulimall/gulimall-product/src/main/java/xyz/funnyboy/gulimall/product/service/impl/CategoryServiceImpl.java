package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.CategoryDao;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.CategoryService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService
{

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
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
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
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

}
