package xyz.funnyboy.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 远程调用对象  成长积分、购物积分
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/02/02
 */
@Data
public class SpuBoundTO
{

    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;
}
