package xyz.funnyboy.gulimall.coupon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.coupon.entity.SeckillSessionEntity;
import xyz.funnyboy.gulimall.coupon.service.SeckillSessionService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:00:18
 */
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController
{
    @Autowired
    private SeckillSessionService seckillSessionService;

    /**
     * 查询三天内需要上架的服务
     *
     * @return
     */
    @GetMapping("/latest3DaySession")
    public R getLatest3DaySession() {
        List<SeckillSessionEntity> sessions = seckillSessionService.getLatest3DaySession();
        return R
                .ok()
                .setData(sessions);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:seckillsession:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = seckillSessionService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:seckillsession:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        SeckillSessionEntity seckillSession = seckillSessionService.getById(id);

        return R
                .ok()
                .put("seckillSession", seckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:seckillsession:save")
    public R save(
            @RequestBody
                    SeckillSessionEntity seckillSession) {
        seckillSessionService.save(seckillSession);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:seckillsession:update")
    public R update(
            @RequestBody
                    SeckillSessionEntity seckillSession) {
        seckillSessionService.updateById(seckillSession);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:seckillsession:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        seckillSessionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
