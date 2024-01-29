package xyz.funnyboy.gulimall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.entity.CommentReplayEntity;
import xyz.funnyboy.gulimall.product.service.CommentReplayService;

import java.util.Arrays;
import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 21:22:07
 */
@RestController
@RequestMapping("product/commentreplay")
public class CommentReplayController
{
    @Autowired
    private CommentReplayService commentReplayService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:commentreplay:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = commentReplayService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:commentreplay:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        CommentReplayEntity commentReplay = commentReplayService.getById(id);

        return R
                .ok()
                .put("commentReplay", commentReplay);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:commentreplay:save")
    public R save(
            @RequestBody
                    CommentReplayEntity commentReplay) {
        commentReplayService.save(commentReplay);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:commentreplay:update")
    public R update(
            @RequestBody
                    CommentReplayEntity commentReplay) {
        commentReplayService.updateById(commentReplay);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:commentreplay:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        commentReplayService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
