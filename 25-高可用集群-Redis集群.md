## 1、创建节点

```bash
# 批量创建 Redis 节点
for port in $(seq 7001 7006); \
do \
mkdir -p /mydata/redis/node-${port}/conf
touch /mydata/redis/node-${port}/conf/redis.conf
cat << EOF >/mydata/redis/node-${port}/conf/redis.conf
port ${port}
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
cluster-announce-ip 192.168.56.10
cluster-announce-port ${port}
cluster-announce-bus-port 1${port}
appendonly yes
EOF
docker run -p ${port}:${port} -p 1${port}:1${port} --name redis-${port} \
-v /mydata/redis/node-${port}/data:/data \
-v /mydata/redis/node-${port}/conf/redis.conf:/etc/redis/redis.conf \
-d redis:5.0.7 redis-server /etc/redis/redis.conf; \
done

# 批量操作 Redis 容器
docker stop $(docker ps -a |grep redis-700 | awk '{ print $1}')
docker stop $(docker ps -a |grep redis-700 | awk '{ print $1}')
docker rm $(docker ps -a |grep redis-700 | awk '{ print $1}')
```



## 2、建立集群

```bash
docker exec -it redis-7001 bash

redis-cli --cluster create \
192.168.56.10:7001 192.168.56.10:7002 192.168.56.10:7003 192.168.56.10:7004 192.168.56.10:7005 192.168.56.10:7006 \
--cluster-replicas 1
```



## 3、测试集群

```
docker exec -it redis-7002 /bin/bash
redis-cli -c -h 192.168.56.10 -p 7006
# 获取集群信息
cluster info
# 获取集群节点
cluster nodes
```

