# Redis 缓存

## 1、整合 Redis

`gulimall-product` - `pom.xml`

```xml
<!-- Redis -->
<!-- lettuce 客户端底层使用 netty 连接 redis（不过我这里使用jmeter压测没有出现异常，不知道是不是因为使用的版本比老师的版本高的原因-->
<!--<dependency>-->
<!--    <groupId>org.springframework.boot</groupId>-->
<!--    <artifactId>spring-boot-starter-data-redis</artifactId>-->
<!--</dependency>-->
<!--
            1)、springboot2.0以后默认使用Lettuce作为操作redis的客户端。它使用netty进行网络通信。
            2)、Lettuce的bug导致netty堆外内存溢出 -Xmx300m; netty 如果没有指定堆外内存，默认使用 -Xmx300m 可以通过 -Dio.netty.maxDirectMemory 进行设置
        解决方案：不能使用 -Dio.netty.maxDirectMemory 只去调大堆外内存。
            1)、升级Lettuce客户端。
            2）、切换使用jedis
        -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <exclusions>
        <exclusion>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```

`application.yml`

```yaml
spring:
  # redis
  redis:
    host: 192.168.56.10
    port: 6379
```



## 2、改造三级分类业务

```java
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        // 从缓存中查询
        final String catalogJson = stringRedisTemplate
                .opsForValue()
                .get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2VO>>>() {});
        }

        // 缓存中没有在从数据库中查询
        final Map<String, List<Catalog2VO>> catalogJsonFromDb = getCatalogJsonFromDb();
        // 同时存入缓存
        stringRedisTemplate
                .opsForValue()
                .set("catalogJson", JSON.toJSONString(catalogJsonFromDb));
        return catalogJsonFromDb;
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDb() {
        // 查询所有分类，并按照父 ID 分组
        final Map<Long, List<CategoryEntity>> categoryMap = baseMapper
                .selectList(null)
                .stream()
                .collect(Collectors.groupingBy(CategoryEntity::getParentCid));
        // 查询一级分类
        return categoryMap
                .get(0L)
                .stream()
                .collect(Collectors.toMap(l1 -> l1
                        .getCatId()
                        .toString(), l1 -> categoryMap
                        .get(l1.getCatId())
                        .stream()
                        .map(l2 -> {
                            final List<Catalog2VO.Catalog3VO> catalog3VOList = categoryMap
                                    .get(l2.getCatId())
                                    .stream()
                                    .map(l3 -> new Catalog2VO.Catalog3VO(l2
                                            .getCatId()
                                            .toString(), l3
                                            .getCatId()
                                            .toString(), l3.getName()))
                                    .collect(Collectors.toList());
                            return new Catalog2VO(l1
                                    .getCatId()
                                    .toString(), catalog3VOList, l2
                                    .getCatId()
                                    .toString(), l2.getName());
                        })
                        .collect(Collectors.toList())));
    }
```



## 3、性能压测

| 压测内容                                    | 压测线程数 | 吞吐量/s | 90%响应时间(ms) | 99%响应时间(ms) |
| ------------------------------------------- | ---------- | -------- | --------------- | --------------- |
| 三级分类数据获取                            | 50         | 6（db）  | 7638            | 7780            |
| 三级分类数据获取（优化业务）                | 50         | 362      | 153             | 192             |
| 三级分类数据获取（使用缓存，lettuce\netty） | 50         | 1025     | 54              | 90              |
| 三级分类数据获取（使用缓存，jedis）         | 50         | 950      | 65              | 101             |

