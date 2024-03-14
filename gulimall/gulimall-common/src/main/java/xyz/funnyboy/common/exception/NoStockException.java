package xyz.funnyboy.common.exception;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 无库存异常
 * @date 2024/03/14
 * @see RuntimeException
 */
public class NoStockException extends RuntimeException
{

    private Long skuId;

    public NoStockException(Long skuId) {
        super(skuId + "号商品没有足够的库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
