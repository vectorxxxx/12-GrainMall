## 1、安装 RabbitMQ

```bash
# 安装 RabbitMQ 镜像
docker run -d --name rabbitmq \
-p 5671:5671 -p 5672:5672 \
-p 4369:4369 -p 25672:25672 \
-p 15671:15671 -p 15672:15672 \
rabbitmq:management 

# 开机自启
docker update rabbitmq --restart=always

# 查看容器
docker images
```

端口含义

| 端口          | 含义                   |
| ------------- | ---------------------- |
| `4369, 25672` | Erlang 发现 & 集群端口 |
| `5672, 5671`  | AMQP 端口              |
| `15672`       | web 管理后台端口       |

