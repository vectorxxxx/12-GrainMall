package xyz.funnyboy.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-12 11:22:53
 */
@Data
public class FareVO
{
    private MemberAddressVO address;

    private BigDecimal fare;
}
