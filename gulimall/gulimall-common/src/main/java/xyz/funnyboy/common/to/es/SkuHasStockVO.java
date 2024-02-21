package xyz.funnyboy.common.to.es;

import lombok.Data;

@Data
public class SkuHasStockVO
{
    private Long skuId;
    private boolean hasStock;

    public boolean getHasStock() {
        return this.hasStock;
    }
}
