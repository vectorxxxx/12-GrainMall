package xyz.funnyboy.gulimall.product.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.SpuInfoEntity;
import xyz.funnyboy.gulimall.product.service.SpuInfoService;
import xyz.funnyboy.gulimall.product.vo.SpuSaveVo;

import java.util.Arrays;
import java.util.Map;

/**
 * spu信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController
{
    @Autowired
    private SpuInfoService spuInfoService;

    @GetMapping("/skuId/{id}")
    public R getSpuInfoBySkuId(
            @PathVariable("id")
                    Long skuId) {
        return R
                .ok()
                .setData(spuInfoService.getSpuInfoBySkuId(skuId));
    }

    /**
     * 商品上架功能
     *
     * @param spuId SPU ID
     * @return {@link R}
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(
            @PathVariable("spuId")
                    Long spuId) {
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:spuinfo:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:spuinfo:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R
                .ok()
                .put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:spuinfo:save")
    public R save(
            @RequestBody
                    SpuSaveVo spuSaveVo) {
        spuInfoService.saveSpuInfo(spuSaveVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:spuinfo:update")
    public R update(
            @RequestBody
                    SpuInfoEntity spuInfo) {
        spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:spuinfo:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
