package xyz.funnyboy.common.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 10:22:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
