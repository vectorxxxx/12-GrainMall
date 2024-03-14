package xyz.funnyboy.gulimall.product.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.SkuInfoEntity;
import xyz.funnyboy.gulimall.product.service.SkuInfoService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

/**
 * sku信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController
{
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}/price")
    public BigDecimal getPrice(
            @PathVariable("skuId")
                    Long skuId) {
        final SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        if (skuInfoEntity == null) {
            return null;
        }
        return skuInfoEntity.getPrice();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:skuinfo:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    // @RequiresPermissions("product:skuinfo:info")
    public R info(
            @PathVariable("skuId")
                    Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R
                .ok()
                .put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:skuinfo:save")
    public R save(
            @RequestBody
                    SkuInfoEntity skuInfo) {
        skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:skuinfo:update")
    public R update(
            @RequestBody
                    SkuInfoEntity skuInfo) {
        skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:skuinfo:delete")
    public R delete(
            @RequestBody
                    Long[] skuIds) {
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
