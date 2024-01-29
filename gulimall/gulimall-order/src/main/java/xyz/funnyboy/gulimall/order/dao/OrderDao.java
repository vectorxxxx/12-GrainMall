package xyz.funnyboy.gulimall.order.dao;

import xyz.funnyboy.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:19:36
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
