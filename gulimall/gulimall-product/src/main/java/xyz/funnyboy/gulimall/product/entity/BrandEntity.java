package xyz.funnyboy.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    private Long brandId;

    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名不能为空")
    private String name;

    /**
     * 品牌logo地址
     */
    @NotEmpty(message = "品牌logo地址不能为空")
    @URL(message = "品牌logo地址格式不正确")
    private String logo;

    /**
     * 介绍
     */
    private String descript;

    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;

    /**
     * 检索首字母
     */
    @NotEmpty(message = "检索首字母不能为空")
    @Pattern(regexp = "/^[a-zA-Z]?$/",
             message = "检索首字母必须是一个字母")
    private String firstLetter;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    @Min(value = 0,
         message = "排序必须大于等于0")
    private Integer sort;

}
