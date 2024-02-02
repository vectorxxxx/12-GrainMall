package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;
import xyz.funnyboy.gulimall.product.entity.AttrEntity;

import java.util.List;

/**
 * 属性分组携带属性
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-01 17:22:57
 */
@Data
public class AttrGroupWithAttrsVo
{
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
