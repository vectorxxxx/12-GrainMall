package xyz.funnyboy.gulimall.ware.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.ware.entity.PurchaseEntity;
import xyz.funnyboy.gulimall.ware.service.PurchaseService;
import xyz.funnyboy.gulimall.ware.vo.MergeVo;
import xyz.funnyboy.gulimall.ware.vo.PurchaseDoneVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController
{
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 完成采购
     */
    @PostMapping("/done")
    //@RequiresPermissions("ware:purchase:list")
    public R received(
            @RequestBody
                    PurchaseDoneVo vo) {
        purchaseService.done(vo);
        return R.ok();
    }

    /**
     * 领取采购单
     */
    @PostMapping("/received")
    //@RequiresPermissions("ware:purchase:list")
    public R received(
            @RequestBody
                    List<Long> ids) {
        purchaseService.received(ids);
        return R.ok();
    }

    /**
     * 合并采购需求
     *
     * @param mergeVo 合并 VO
     * @return {@link R}
     */
    @PostMapping("/merge")
    //@RequiresPermissions("ware:purchase:list")
    public R merge(
            @RequestBody
                    MergeVo mergeVo) {
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }

    /**
     * 查询未领取的采购单
     *
     * @param params 参数
     * @return {@link R}
     */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:purchase:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:purchase:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R
                .ok()
                .put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:purchase:save")
    public R save(
            @RequestBody
                    PurchaseEntity purchase) {
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:purchase:update")
    public R update(
            @RequestBody
                    PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:purchase:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
