# Session 共享

## 1、依赖

```xml
<!-- Spring Session -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```



## 2、启动类

```java
// 整合Redis作为session存储
@EnableRedisHttpSession
```



## 3、配置文件

```yaml
spring:
  # redis
  redis:
    host: 192.168.56.10
    port: 6379
  # 使用 redis 存储 session
  session:
    store-type: redis

server:
  # 配置session过期时间
  servlet:
    session:
      timeout:30m
```



## 4、配置类

`GulimallSessionConfig`

```java
@Configuration
public class GulimallSessionConfig
{
    @Bean
    public CookieSerializer cookieSerializer() {
        final DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setDomainName("gulimall.com");
        serializer.setCookieName("GULISESSION");
        return serializer;
    }

    /**
     * 自定义序列化机制
     * <p>
     * 这里方法名必须是：springSessionDefaultRedisSerializer
     *
     * @return {@link RedisSerializer}<{@link Object}>
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

