package xyz.funnyboy.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.member.entity.MemberEntity;
import xyz.funnyboy.gulimall.member.vo.MemberLoginVO;
import xyz.funnyboy.gulimall.member.vo.MemberRegistVO;
import xyz.funnyboy.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:11:19
 */
public interface MemberService extends IService<MemberEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     *
     * @param memberRegistVO 会员注册员 VO
     */
    void register(MemberRegistVO memberRegistVO);

    /**
     * 登录
     *
     * @param vo 账户登录用户
     * @return {@link MemberEntity}
     */
    MemberEntity login(MemberLoginVO vo);

    /**
     * 登录
     *
     * @param socialUser 社交用户
     * @return {@link MemberEntity}
     */
    MemberEntity login(SocialUser socialUser);
}

