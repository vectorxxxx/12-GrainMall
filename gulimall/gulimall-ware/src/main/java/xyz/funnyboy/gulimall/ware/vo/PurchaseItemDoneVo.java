package xyz.funnyboy.gulimall.ware.vo;

import lombok.Data;

/**
 * 采购项
 */
@Data
public class PurchaseItemDoneVo
{
    /**
     * "itemId":1,"status":3,"reason":"",
     * <p>
     * "itemId":3,"status":4,"reason":"无货"
     */
    private Long itemId;

    private Integer status;

    private String reason;
}
