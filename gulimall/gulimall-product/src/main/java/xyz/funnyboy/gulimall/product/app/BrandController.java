package xyz.funnyboy.gulimall.product.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.common.validator.group.AddGroup;
import xyz.funnyboy.common.validator.group.UpdateGroup;
import xyz.funnyboy.common.validator.group.UpdateStatusGroup;
import xyz.funnyboy.gulimall.product.entity.BrandEntity;
import xyz.funnyboy.gulimall.product.service.BrandService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/brand")
public class BrandController
{
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(
            @PathVariable("brandId")
                    Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R
                .ok()
                .put("brand", brand);
    }

    @RequestMapping("/info/ids")
    public R infoByIds(
            @RequestParam("brandIds")
                    List<Long> brandIds) {
        List<BrandEntity> brandEntityList = brandService.queryByIds(brandIds);
        return R
                .ok()
                .put("brand", brandEntityList);
    }

    /**
     * 保存(V3: JSR303校验，定义了统一异常处理且进行了分组校验时的写法)
     */
    @PostMapping("/save")
    // @RequiresPermissions("product:brand:save")
    public R save(
            @Validated(AddGroup.class)
            @RequestBody
                    BrandEntity brand) {
        brandService.save(brand);

        return R.ok();
    }

    // /**
    //  * 保存(V2: JSR303校验，定义了统一异常处理但未进行分组校验时的写法)
    //  */
    // @PostMapping("/save")
    // // @RequiresPermissions("product:brand:save")
    // public R save(@Valid
    //               @RequestBody
    //                       BrandEntity brand) {
    //     brandService.save(brand);
    //
    //     return R.ok();
    // }

    //
    // /**
    //  * 保存(V1: JSR303校验，未定义统一异常处理和进行分组校验时的写法)
    //  */
    // @PostMapping("/save")
    // // @RequiresPermissions("product:brand:save")
    // public R save(@Valid
    //               @RequestBody
    //                       BrandEntity brand, BindingResult result) {
    //     if (result.hasErrors()) {
    //         final Map<String, String> map = result
    //                 .getFieldErrors()
    //                 .stream()
    //                 .filter(fieldError -> !StringUtils.isEmpty(fieldError.getDefaultMessage()) && !StringUtils.isEmpty(fieldError.getField()))
    //                 .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    //         return R
    //                 .error(400, "提交的数据不合法")
    //                 .put("data", map);
    //     }
    //     brandService.save(brand);
    //
    //     return R.ok();
    // }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(
            @Validated(UpdateGroup.class)
            @RequestBody
                    BrandEntity brand) {
        // 要对品牌（分类）名字进行修改时，品牌分类关系表之中的名字也要进行修改
        brandService.updateByIdDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(
            @Validated(UpdateStatusGroup.class)
            @RequestBody
                    BrandEntity brand) {
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(
            @RequestBody
                    Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
