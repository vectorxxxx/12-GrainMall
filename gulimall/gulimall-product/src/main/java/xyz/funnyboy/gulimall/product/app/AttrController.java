package xyz.funnyboy.gulimall.product.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.ProductAttrValueEntity;
import xyz.funnyboy.gulimall.product.service.AttrService;
import xyz.funnyboy.gulimall.product.service.ProductAttrValueService;
import xyz.funnyboy.gulimall.product.vo.AttrRespVo;
import xyz.funnyboy.gulimall.product.vo.AttrVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/attr")
public class AttrController
{
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(
            @PathVariable("spuId")
                    Long spuId,
            @RequestBody
                    List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId, entities);
        return R.ok();
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R baseListforspu(
            @PathVariable("spuId")
                    Long spuId) {
        return R
                .ok()
                .put("data", productAttrValueService.baseAttrlistForSpu(spuId));
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(
            @RequestParam
                    Map<String, Object> params,
            @PathVariable("catelogId")
                    Long catelogId,

            @PathVariable("attrType")
                    String attrType) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attr:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(
            @PathVariable("attrId")
                    Long attrId) {
        AttrRespVo attr = attrService.getAttrInfo(attrId);

        return R
                .ok()
                .put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(
            @RequestBody
                    AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attr:update")
    public R update(
            @RequestBody
                    AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(
            @RequestBody
                    Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
