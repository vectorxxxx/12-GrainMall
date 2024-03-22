package xyz.funnyboy.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:00:18
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询三天内需要上架的服务
     *
     * @return {@link List}<{@link SeckillSessionEntity}>
     */
    List<SeckillSessionEntity> getLatest3DaySession();
}

