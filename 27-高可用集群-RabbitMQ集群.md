## 1、搭建集群

```bash
mkdir /mydata/rabbitmq

cd rabbitmq/

mkdir rabbitmq01 rabbitmq02 rabbitmq03

docker run -d --hostname rabbitmq01 --name rabbitmq01 \
-v /mydata/rabbitmq/rabbitmq01:/var/lib/rabbitmq \
-p 15673:15672 -p 5673:5672 \
-e RABBITMQ_ERLANG_COOKIE='funnyboy' \
rabbitmq:management

docker run -d --hostname rabbitmq02 --name rabbitmq02 \
-v /mydata/rabbitmq/rabbitmq02:/var/lib/rabbitmq \
-p 15674:15672 -p 5674:5672 \
-e RABBITMQ_ERLANG_COOKIE='funnyboy' \
--link rabbitmq01:rabbitmq01 \
rabbitmq:management

docker run -d --hostname rabbitmq03 --name rabbitmq03 \
-v /mydata/rabbitmq/rabbitmq03:/var/lib/rabbitmq \
-p 15675:15672 -p 5675:5672 \
-e RABBITMQ_ERLANG_COOKIE='funnyboy' \
--link rabbitmq01:rabbitmq01 \
--link rabbitmq02:rabbitmq02 \
rabbitmq:management
```



## 2、加入集群

### 2.1、第一个节点

```bash
docker exec -it rabbitmq01 /bin/bash

rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app

Exit
```

### 2.2、第二个节点

```bash
docker exec -it rabbitmq02 /bin/bash

rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbitmq01
rabbitmqctl start_app

exit
```

### 2.3、第三个节点

```bash
docker exec -it rabbitmq03 bash

rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbitmq01
rabbitmqctl start_app

exit
```



## 3、镜像集群

```bash
docker exec -it rabbitmq01 bash

# 设置
rabbitmqctl set_policy -p / ha "^" '{"ha-mode":"all","ha-sync-mode":"automatic"}' 


rabbitmqctl list_policies -p /
```

