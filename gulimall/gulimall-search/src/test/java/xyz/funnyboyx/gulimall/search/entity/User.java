package xyz.funnyboyx.gulimall.search.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户测试类
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-05 23:18:32
 */
@Data
@NoArgsConstructor
// 链式调用
@Accessors(chain = true)
public class User
{
    private String name;
    private Integer age;
    private String gender;
}
