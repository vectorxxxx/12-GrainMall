## 1、Nacos

```bash
docker run --name nacos \
--env MODE=standalone \
-v /mydata/nacos/conf:/home/nacos/conf \
-d -p 8848:8848 \
nacos/nacos-server:1.1.4
```



## 2、Sentinel

```bash
docker run --name sentinel \
-d -p 8858:8858 \
-d bladex/sentinel-dashboard:1.6.3
```



## 3、Zipkin

```bash
docker run -d -p 9411:9411 openzipkin/zipkin

docker run --env STORAGE_TYPE=elasticsearch --env ES_HOSTS=192.168.56.10:9200 openzipkin/zipkin
```

