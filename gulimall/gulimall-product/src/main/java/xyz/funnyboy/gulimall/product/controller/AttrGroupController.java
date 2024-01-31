package xyz.funnyboy.gulimall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.AttrGroupEntity;
import xyz.funnyboy.gulimall.product.service.AttrGroupService;
import xyz.funnyboy.gulimall.product.service.CategoryService;

import java.util.Arrays;
import java.util.Map;

/**
 * 属性分组
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController
{
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(
            @PathVariable(value = "catelogId",
                          required = false)
                    Long catelogId,
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(
            @PathVariable("attrGroupId")
                    Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        // 获取当前分类的完整路径
        Long[] catelogPath = categoryService.findCatelogPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(catelogPath);

        return R
                .ok()
                .put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(
            @RequestBody
                    AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(
            @RequestBody
                    AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(
            @RequestBody
                    Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
