package xyz.funnyboy.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;

/**
 * 订单
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:19:36
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity>
{

    /**
     * 更新订单状态
     *
     * @param orderSn 订单号
     * @param code    订单状态
     * @param payType 支付类型
     */
    void updateOrderStatus(
            @Param("orderSn")
                    String orderSn,
            @Param("code")
                    Integer code,
            @Param("payType")
                    Integer payType);
}
