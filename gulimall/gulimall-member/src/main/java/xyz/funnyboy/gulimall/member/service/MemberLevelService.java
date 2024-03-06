package xyz.funnyboy.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:11:19
 */
public interface MemberLevelService extends IService<MemberLevelEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取默认级别
     *
     * @return {@link Long}
     */
    Long getDefaultLevel();
}

