# Sentinel

## 1、依赖

```xml
<!-- 引入sentinel网关限流 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>
</dependency>
```



## 2、配置文件

`application.yml`

```yaml
spring:
    # sentinel
    sentinel:
      transport:
        # 控制台地址
        dashboard: localhost:8080
        port: 8719

# sentinel是不会对feign进行监控的，需要开启配置
feign:
  sentinel:
    enabled: true

# Spring Boot声明哪些端点是可公开的，即暴露给外部访问的。
# 默认情况下会将所有端点都暴露出去，包括health、info等常用端点，这在生产环境中可能会存在一定的安全隐患。
# 一般暴露health,info就行了
management:
  endpoints:
    web:
      exposure:
        include: '*'
```



## 3、代码

`SeckillFeignService`

```java
@FeignClient(value = "gulimall-seckill",
             fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService
{
    // ...
}
```

`SeckillFeignServiceFallBack`

```java
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService
{
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.error("熔断方法调用...getSkuSeckillInfo");
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
    }
}
```

`SeckillSentinelUrlBlockHandler`

```java
@Component
public class SeckillSentinelUrlBlockHandler implements BlockExceptionHandler
{

    /**
     * 自定义限流返回信息
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        // 降级业务处理
        R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response
                .getWriter()
                .write(JSON.toJSONString(error));
    }
}
```

`SeckillServiceImpl`

```java
public List<SeckillSkuRedisTO> blockHandler(BlockException e) {
    log.error("getCurrentSeckillSkus()方法被限流/降级/系统保护");
    return null;
}

@SentinelResource(value = "getCurrentSeckillSkusResource",
                  blockHandler = "blockHandler")
@Override
public List<SeckillSkuRedisTO> getCurrentSeckillSkus() {
    /
}
```

