package xyz.funnyboy.common.to.mq;

import lombok.Data;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 10:22:59
 */
@Data
public class StockLockedTO
{
    /**
     * 库存工作单id
     */
    private Long id;

    /**
     * 工作详情id
     */
    private StockDetailTO detailTO;
}
