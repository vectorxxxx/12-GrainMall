package xyz.funnyboy.gulimall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.SpuInfoDescEntity;
import xyz.funnyboy.gulimall.product.service.SpuInfoDescService;

import java.util.Arrays;
import java.util.Map;

/**
 * spu信息介绍
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/spuinfodesc")
public class SpuInfoDescController
{
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:spuinfodesc:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = spuInfoDescService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{spuId}")
    // @RequiresPermissions("product:spuinfodesc:info")
    public R info(
            @PathVariable("spuId")
                    Long spuId) {
        SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(spuId);

        return R
                .ok()
                .put("spuInfoDesc", spuInfoDesc);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:spuinfodesc:save")
    public R save(
            @RequestBody
                    SpuInfoDescEntity spuInfoDesc) {
        spuInfoDescService.save(spuInfoDesc);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:spuinfodesc:update")
    public R update(
            @RequestBody
                    SpuInfoDescEntity spuInfoDesc) {
        spuInfoDescService.updateById(spuInfoDesc);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:spuinfodesc:delete")
    public R delete(
            @RequestBody
                    Long[] spuIds) {
        spuInfoDescService.removeByIds(Arrays.asList(spuIds));

        return R.ok();
    }

}
