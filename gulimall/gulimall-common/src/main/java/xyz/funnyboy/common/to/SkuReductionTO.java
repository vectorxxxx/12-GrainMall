package xyz.funnyboy.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author VectorX
 * @version 1.0.0
 * @date 2024/02/02
 */
@Data
public class SkuReductionTO
{

    private Long skuId;

    /***
     * fullCount、discount、countStatus  打折信息
     * 买几件、打几折、是否参数其他优惠
     */
    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;
}
