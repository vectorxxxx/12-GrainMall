package xyz.funnyboy.gulimall.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.member.entity.MemberEntity;
import xyz.funnyboy.gulimall.member.feign.CouponFeignService;
import xyz.funnyboy.gulimall.member.service.MemberService;

import java.util.Arrays;
import java.util.Map;

/**
 * 会员
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:11:19
 */
@RestController
@RequestMapping("member/member")
public class MemberController
{
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("会员昵称张三");
        R membercoupons = couponFeignService.membercoupons();//假设张三去数据库查了后返回了张三的优惠券信息

        //打印会员和优惠券信息
        return R
                .ok()
                .put("member", memberEntity)
                .put("coupons", membercoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(
            @PathVariable("id")
                    Long id) {
        MemberEntity member = memberService.getById(id);

        return R
                .ok()
                .put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(
            @RequestBody
                    MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(
            @RequestBody
                    MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(
            @RequestBody
                    Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}