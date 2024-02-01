package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;

/**
 * 属性分组-属性 关联VO
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-01 14:45:07
 */
@Data
public class AttrGroupRelationVo
{
    /**
     * 属性 ID
     */
    private Long attrId;
    /**
     * 属性分组 ID
     */
    private Long attrGroupId;
}
