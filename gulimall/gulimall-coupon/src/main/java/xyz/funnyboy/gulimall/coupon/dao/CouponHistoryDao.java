package xyz.funnyboy.gulimall.coupon.dao;

import xyz.funnyboy.gulimall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:00:18
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
