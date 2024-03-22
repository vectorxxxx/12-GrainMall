# Nginx 动静分离

## 1、动静分离

1、删除 gulimall-product 中 resources/static 下的所有静态资源

2、修改 index.html 中资源引用路径

```
"index/
全部替换为
"static/index/
```

3、修改 Nginx 配置文件

```bash
vi gulimall.conf
```

`/mydata/nginx/conf/conf.d/gulimall.conf`

```bash
server {
    listen       80;
    server_name  gulimall.com;

	# 添加静态资源访问请求转发
    location /static/ {
        root   /usr/share/nginx/html;
    }

    location / {
        proxy_set_header Host $host;
        proxy_pass http://gulimall;
    }
    
    # 其他省略......
}
```

4、重启 Nginx

```bash
docker restart nginx
```

5、访问验证 [http://gulimall.com/](http://gulimall.com/)

![image-20240224192123644](https://s2.loli.net/2024/02/24/RtUP1JVAqYwjTyu.png)



## 2、性能压测

性能调优参数

```bash
-Xms1024m -Xmx1024m -Xmn512m
```

压测结果（以下结果均为直接访问微服务得到，不通过 Nginx 和网关）

| 压测内容                     | 压测线程数 | 吞吐量/s | 90%响应时间(ms) | 99%响应时间(ms) |
| ---------------------------- | ---------- | -------- | --------------- | --------------- |
| 首页全量数据获取             | 50         | 32       | 1624            | 1946            |
| 首页全量数据获取（动静分离） | 50         | 48       | 1060            | 1603            |
| 首页全量数据获取（性能调优） | 50         | 64       | 903             | 1066            |

