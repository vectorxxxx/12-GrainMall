package xyz.funnyboy.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 无库存异常
 * @date 2024/03/14
 * @see RuntimeException
 */
public class NoStockException extends RuntimeException
{

    @Getter
    @Setter
    private Long skuId;

    public NoStockException(Long skuId) {
        super(skuId + "号商品没有足够的库存了");
    }

    public NoStockException(Set<Long> keySet) {
        super("商品id：" + StringUtils.join(keySet, ",") + "库存不足！");
    }
}
