package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-04 22:37:24
 */
@Data
public class SkuItemSaleAttrVO
{
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVO> attrValues;
}
