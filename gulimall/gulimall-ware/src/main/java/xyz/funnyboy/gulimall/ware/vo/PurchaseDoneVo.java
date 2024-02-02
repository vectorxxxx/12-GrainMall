package xyz.funnyboy.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 采购单
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/02/03
 */
@Data
public class PurchaseDoneVo
{

    /**
     * 采购单id
     */
    @NotNull
    private Long id;

    /**
     * 采购项(需求)
     */
    private List<PurchaseItemDoneVo> items;
}
