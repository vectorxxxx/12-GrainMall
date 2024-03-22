package xyz.funnyboy.gulimall.seckill.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-20 17:42:53
 */
@Data
public class SeckillSessionWithSkusTO
{
    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SeckillSkuVO> relationSkus;
}
