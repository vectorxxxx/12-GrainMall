package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;

/**
 * 属性响应返回
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 23:25:47
 */
@Data
public class AttrRespVo extends AttrVo
{
    /**
     * 分类名称
     */
    private String catelogName;

    /**
     * 属性分组名称
     */
    private String groupName;

    /**
     * 分类名称路径
     */
    private Long[] catelogPath;
}
