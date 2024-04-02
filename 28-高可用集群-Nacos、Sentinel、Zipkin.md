## 1、Nacos

```bash
docker run --name nacos \
--env MODE=standalone \
-v /mydata/nacos/conf:/home/nacos/conf \
-d -p 8848:8848 \
--restart=always \
nacos/nacos-server:1.1.4
```

`application.properties`

```properties
spring.cloud.nacos.discovery.server-addr=192.168.56.10:8848
```



## 2、Sentinel

```bash
docker run --name sentinel \
-d -p 8858:8858 \
--restart=always \
bladex/sentinel-dashboard:1.6.3 
```

`application.properties`

```properties
spring.cloud.sentinel.transport.dashboard=192.168.56.10:8858
management.endpoints.web.exposure.include=*
feign.sentinel.enabled=true
```



## 3、Zipkin

```bash
docker run -d -p 9411:9411 --restart=always openzipkin/zipkin

docker run --env STORAGE_TYPE=elasticsearch --env ES_HOSTS=192.168.56.10:9200 openzipkin/zipkin
```

`application.properties`

```properties
spring.zipkin.base-url=http://192.168.56.10:9411/
spring.zipkin.discoveryClientEnabled=false
spring.zipkin.sender.type=web
```

