package xyz.funnyboy.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description 商品信息
 * @date 2024-03-11 16:22:06
 */
@Data
public class OrderItemVO
{
    private Long skuId;

    /**
     * 标题
     */
    private String title;

    /**
     * 图像
     */
    private String image;

    /**
     * 销售属性
     */
    private List<String> skuAttr;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 有库存
     */
    // private boolean hasStock;

    /**
     * 重量
     */
    private BigDecimal weight;
}
