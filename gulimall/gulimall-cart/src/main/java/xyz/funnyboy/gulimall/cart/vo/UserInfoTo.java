package xyz.funnyboy.gulimall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 10:17:59
 */
@Data
@ToString
public class UserInfoTo
{
    private Long userId;
    private String userKey;

    private Boolean tempUser = false;   // 判断是否有临时用户
}
