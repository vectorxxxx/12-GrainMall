package xyz.funnyboy.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-14 10:48:10
 */
@Data
public class SkuWareHasStock
{
    private Long skuId;

    private List<Long> wareId;

    private Integer num;
}
