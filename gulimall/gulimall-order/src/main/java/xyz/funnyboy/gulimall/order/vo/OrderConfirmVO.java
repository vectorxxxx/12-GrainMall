package xyz.funnyboy.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 16:09:30
 */
@ToString
public class OrderConfirmVO
{
    /**
     * 收货地址，ums_member_receive_address 表
     */
    @Setter
    @Getter
    List<MemberAddressVO> address;

    /**
     * 所有选中的购物车项
     */
    @Setter
    @Getter
    List<OrderItemVO> items;

    /**
     * 积分/优惠券信息
     */
    @Setter
    @Getter
    Integer integration;

    /**
     * 防重令牌
     */
    @Setter
    @Getter
    private String orderToken;

    @Setter
    @Getter
    Map<Long, Boolean> stocks;

    /**
     * 获取商品总价格
     */
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items == null) {
            return sum;
        }

        return items
                .stream()
                .map(item -> item
                        .getPrice()
                        .multiply(new BigDecimal(item
                                .getCount()
                                .toString())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 应付的价格
     */
    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public Integer getCount() {
        Integer i = 0;
        if (items == null) {
            return i;
        }

        return items
                .stream()
                .mapToInt(OrderItemVO::getCount)
                .sum();
    }
    /**
     * 发票信息...
     */

}
