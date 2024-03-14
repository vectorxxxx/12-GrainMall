package xyz.funnyboy.gulimall.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author VectorX
 * @version V1.0
 * @description feign的request请求配置
 * @date 2024-03-11 17:14:25
 */
@Configuration
public class GulimallFeignConfig
{
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // RequestContextHolder拿到刚进来时的请求
            final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                final HttpServletRequest request = attributes
                        // 获取原先请求
                        .getRequest();
                final String cookie = request
                        // 同步请求头数据：Cookie
                        .getHeader("Cookie");
                requestTemplate
                        // 给新请求同步旧请求的Cookie
                        .header("Cookie", cookie);
            }
        };
    }
}
