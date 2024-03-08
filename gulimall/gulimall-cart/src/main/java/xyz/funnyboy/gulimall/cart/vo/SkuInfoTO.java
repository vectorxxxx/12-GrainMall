package xyz.funnyboy.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 11:12:03
 */
@Data
public class SkuInfoTO
{
    private Long skuId;
    private Long spuId;
    private String skuName;

    /**
     * sku介绍描述
     */
    private String skuDesc;
    private Long catalogId;
    private Long brandId;
    private String skuDefaultImg;
    private String skuTitle;
    private String skuSubtitle;
    private BigDecimal price;
    /**
     * 销量
     */
    private Long saleCount;
}
