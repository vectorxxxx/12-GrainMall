package xyz.funnyboy.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import xyz.funnyboy.common.validator.ListValue;
import xyz.funnyboy.common.validator.group.AddGroup;
import xyz.funnyboy.common.validator.group.UpdateGroup;
import xyz.funnyboy.common.validator.group.UpdateStatusGroup;

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
    @NotNull(message = "修改必须定制品牌id",
             groups = {UpdateGroup.class})
    @Null(message = "新增不能指定id",
          groups = {AddGroup.class})
    @TableId
    private Long brandId;

    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名不能为空",
              groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 品牌logo地址
     */
    @NotEmpty(message = "品牌logo地址不能为空",
              groups = {AddGroup.class})
    @URL(message = "品牌logo地址格式不正确",
         groups = {AddGroup.class, UpdateGroup.class})
    private String logo;

    /**
     * 介绍
     */
    private String descript;

    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(message = "显示状态不能为空",
             groups = {AddGroup.class, UpdateStatusGroup.class})
    @ListValue(values = {0, 1},
               groups = {AddGroup.class, UpdateGroup.class, UpdateStatusGroup.class})
    private Integer showStatus;

    /**
     * 检索首字母
     */
    @NotEmpty(message = "检索首字母不能为空",
              groups = {AddGroup.class})
    @Pattern(regexp = "/^[a-zA-Z]?$/",
             message = "检索首字母必须是一个字母",
             groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空",
             groups = {AddGroup.class})
    @Min(value = 0,
         message = "排序必须大于等于0",
         groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;

}
