package xyz.funnyboy.gulimall.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-07 14:14:35
 */
@Data
@Component
@ConfigurationProperties(prefix = "gitee.oauth")
public class GiteeOAuthConfig
{
    private String host;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String responseType;
    private String grantType;
    private String failPath;
    private String successPath;
}
