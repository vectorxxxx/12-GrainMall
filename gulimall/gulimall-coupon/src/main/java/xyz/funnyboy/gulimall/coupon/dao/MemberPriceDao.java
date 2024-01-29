package xyz.funnyboy.gulimall.coupon.dao;

import xyz.funnyboy.gulimall.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:00:18
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
