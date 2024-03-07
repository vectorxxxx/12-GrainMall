package xyz.funnyboy.gulimall.member.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-07 11:04:23
 */
@Data
@ToString
public class SocialUser
{
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String scope;
    private String created_at;
    private Integer id;
    private String name;
}
