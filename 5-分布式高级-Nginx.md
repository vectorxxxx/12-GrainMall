# Nginx

## 1、修改 hosts

直接以管理员身份修改 `C:\Windows\System32\drivers\etc\hosts` 或者借助工具 `SwitchHosts` 进行配置切换

```bash
# 添加
192.168.56.10 gulimall.com
```



## 2、启动 Nginx

```bash
# 设置 nginx 自启动
docker update nginx --restart=always

# 启动 nginx
docker start nginx
```

访问验证 [http://gulimall.com/](http://gulimall.com/)

![image-20240223204906950](https://s2.loli.net/2024/02/23/dwrAFO1LgVJ5Tn4.png)



## 3、配置 Nginx

```bash
# 进入 /mydata/nginx/conf/conf.d
cd /mydata/nginx/conf/conf.d

# 复制 default.conf 为 gulimall.conf
cp default.conf gulimall.conf

# 修改 gulimall.conf
vi gulimall.conf
```

`/mydata/nginx/conf/conf.d/gulimall.conf`

```bash
server {
    listen       80;
    server_name  gulimall.com;

    location / {
        proxy_pass http://192.168.56.1:10000;
    }

    # 其余省略......
}
```

重启 nginx

```bash
docker restart nginx
```

访问验证 [http://gulimall.com/](http://gulimall.com/)

![image-20240223205934321](https://s2.loli.net/2024/02/23/2SLhiGe8mgl3qMW.png)



## 4、网关映射

```bash
# 修改 nginx.conf
vi nginx.conf
```

`/mydata/nginx/conf/nginx.conf`

参考：[Using nginx as HTTP load balancer](https://nginx.org/en/docs/http/load_balancing.html)

```bash
http {
    # 其余省略......

    upstream gulimall {
        server 192.168.56.1:88;
    }

    include /etc/nginx/conf.d/*.conf;
}
```

`/mydata/nginx/conf/conf.d/gulimall.conf`

```bash
server {
    listen       80;
    server_name  gulimall.com;

    location / {
    	proxy_set_header Host $host;
        proxy_pass http://gulimall;
    }

    # 其余省略......
}
```

`gulimall-gateway` - `application.yml`

参考：[The Host Route Predicate Factory](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-host-route-predicate-factory)

```yaml
# 主机路由（注意一定要放置在所有路由规则的最后，因为上述路由会对请求进行截串处理。如果放置在配置文件的最上面，就会使上述路由规则失效）
- id: gulimall_host_route
  uri: lb://gulimall-product
  predicates:
    - Host=**.gulimall.com,gulimall.com
```

重启 nginx

```bash
docker restart nginx
```

访问验证 [http://gulimall.com/](http://gulimall.com/)

![image-20240223205934321](https://s2.loli.net/2024/02/23/2SLhiGe8mgl3qMW.png)

查看 `gulimall-gateway` 控制台

<details><summary><font size="3" color="orange">输出日志</font></summary> 
<pre><code class="language-json">2024-02-23 22:03:42.699  INFO 5352 --- [ctor-http-nio-2] c.netflix.config.ChainedDynamicProperty  : Flipping property: gulimall-product.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
2024-02-23 22:03:42.700  INFO 5352 --- [ctor-http-nio-2] c.netflix.loadbalancer.BaseLoadBalancer  : Client: gulimall-product instantiated a LoadBalancer: DynamicServerListLoadBalancer:{NFLoadBalancer:name=gulimall-product,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
2024-02-23 22:03:42.700  INFO 5352 --- [ctor-http-nio-2] c.n.l.DynamicServerListLoadBalancer      : Using serverListUpdater PollingServerListUpdater
2024-02-23 22:03:42.701  INFO 5352 --- [ctor-http-nio-2] c.netflix.config.ChainedDynamicProperty  : Flipping property: gulimall-product.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
2024-02-23 22:03:42.702  INFO 5352 --- [ctor-http-nio-2] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client gulimall-product initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=gulimall-product,current list of Servers=[192.168.56.1:10000],Load balancer stats=Zone stats: {unknown=[Zone:unknown;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:192.168.56.1:10000;	Zone:UNKNOWN;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 08:00:00 CST 1970;	First connection made: Thu Jan 01 08:00:00 CST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
]}ServerList:com.alibaba.cloud.nacos.ribbon.NacosServerList@6db65e64
2024-02-23 22:03:43.702  INFO 5352 --- [erListUpdater-1] c.netflix.config.ChainedDynamicProperty  : Flipping property: gulimall-product.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
</code></pre></details>

访问验证 [http://gulimall.com/api/product/attrattrgrouprelation/list](http://gulimall.com/api/product/attrattrgrouprelation/list)

![image-20240223221212494](https://s2.loli.net/2024/02/23/OokTbZgBCRJV7iw.png)

