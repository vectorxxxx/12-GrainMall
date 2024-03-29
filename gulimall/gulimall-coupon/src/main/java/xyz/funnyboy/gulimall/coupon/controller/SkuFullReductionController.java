package xyz.funnyboy.gulimall.coupon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.to.SkuReductionTO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.coupon.entity.SkuFullReductionEntity;
import xyz.funnyboy.gulimall.coupon.service.SkuFullReductionService;

import java.util.Arrays;
import java.util.Map;

/**
 * 商品满减信息
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:00:18
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController
{
    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @PostMapping("/saveinfo")
    public R saveSkuReduction(
            @RequestBody
                    SkuReductionTO skuReductionTO) {
        skuFullReductionService.saveSkuReduction(skuReductionTO);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:skufullreduction:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = skuFullReductionService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:skufullreduction:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return R
                .ok()
                .put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:skufullreduction:save")
    public R save(
            @RequestBody
                    SkuFullReductionEntity skuFullReduction) {
        skuFullReductionService.save(skuFullReduction);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:skufullreduction:update")
    public R update(
            @RequestBody
                    SkuFullReductionEntity skuFullReduction) {
        skuFullReductionService.updateById(skuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:skufullreduction:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        skuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
