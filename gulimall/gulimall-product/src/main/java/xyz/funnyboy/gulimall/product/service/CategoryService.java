package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface CategoryService extends IService<CategoryEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查出所有分类 以及子分类，以树形结构组装起来
     *
     * @return {@link List}<{@link CategoryEntity}>
     */
    List<CategoryEntity> listWithTree();

    /**
     * 按 ID 删除菜单
     *
     * @param ids IDS
     */
    void removeMenuByIds(List<Long> ids);

    /**
     * 查找 Catelog 路径
     *
     * @param catelogId 分类 ID
     * @return {@link Long[]}
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 查找 Catelog 路径名
     *
     * @param catelogId 分类 ID
     * @return {@link String}
     */
    String findCatelogPathName(Long catelogId);

    /**
     * 级联更新数据
     *
     * @param category 类别
     */
    void updateCascade(CategoryEntity category);
}

