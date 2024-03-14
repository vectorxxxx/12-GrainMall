package xyz.funnyboy.gulimall.ware.vo;

import lombok.Data;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-12 15:02:29
 */
@Data
public class LockStockResult
{
    private Long skuId;

    private Integer num;

    private Boolean locked;
}
