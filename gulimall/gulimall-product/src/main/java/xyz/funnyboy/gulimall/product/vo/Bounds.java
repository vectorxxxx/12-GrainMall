package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description：成直积分、购物积分
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/02/02
 */
@Data
public class Bounds
{

    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
