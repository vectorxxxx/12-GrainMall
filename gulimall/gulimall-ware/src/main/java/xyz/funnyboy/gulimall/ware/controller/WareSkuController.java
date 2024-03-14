package xyz.funnyboy.gulimall.ware.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.to.es.SkuHasStockVO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;
import xyz.funnyboy.gulimall.ware.service.WareSkuService;
import xyz.funnyboy.gulimall.ware.vo.WareSkuLockVO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController
{
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/order")
    public R orderLockStock(
            @RequestBody
                    WareSkuLockVO vo) {
        try {
            Boolean stock = wareSkuService.orderLockStock(vo);
            return R.ok();
        }
        catch (Exception e) {
            return R.error(BizCodeEnum.NOT_STOCK_EXCEPTION.getCode(), BizCodeEnum.NOT_STOCK_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/hasStock")
    public R hasStock(
            @RequestBody
                    List<Long> skuIds) {
        final List<SkuHasStockVO> hasStockToList = wareSkuService.getSkuHasStock(skuIds);
        return R
                .ok()
                .setData(hasStockToList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:waresku:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:waresku:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R
                .ok()
                .put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:waresku:save")
    public R save(
            @RequestBody
                    WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:waresku:update")
    public R update(
            @RequestBody
                    WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:waresku:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
