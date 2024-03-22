# Feign 请求头

## 1、配置类

`GulimallFeignConfig`

```java
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
```

