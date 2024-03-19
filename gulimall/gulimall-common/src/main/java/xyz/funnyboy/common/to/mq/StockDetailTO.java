package xyz.funnyboy.common.to.mq;

import lombok.Data;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 10:22:18
 */
@Data
public class StockDetailTO
{

    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;

}
