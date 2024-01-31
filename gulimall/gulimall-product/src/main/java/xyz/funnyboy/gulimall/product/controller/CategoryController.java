package xyz.funnyboy.gulimall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("/product/category")
public class CategoryController
{
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list/tree")
    public R list() {
        List<CategoryEntity> categoryEntityList = categoryService.listWithTree();
        return R
                .ok()
                .put("data", categoryEntityList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:category:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = categoryService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{catId}")
    // @RequiresPermissions("product:category:info")
    public R info(
            @PathVariable("catId")
                    Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R
                .ok()
                .put("data", category);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("product:category:save")
    public R save(
            @RequestBody
                    CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("product:category:update")
    public R update(
            @RequestBody
                    CategoryEntity category) {
        categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 更新排序
     *
     * @param menus 菜单
     * @return {@link R}
     */
    @PutMapping("/update/sort")
    public R updateSort(
            @RequestBody
                    CategoryEntity[] menus) {
        for (CategoryEntity menu : menus) {
            System.out.println(menu);
        }
        categoryService.updateBatchById(Arrays.asList(menus));
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    // @RequiresPermissions("product:category:delete")
    public R delete(
            @RequestBody
                    Long[] catIds) {
        categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
