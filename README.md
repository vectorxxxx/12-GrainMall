# 分布式基础

## 一、Demo 效果

### 1、后台管理登录

![image-20240203161758264](https://s2.loli.net/2024/02/03/X5gTfLB48NKoD7p.png)

### 2、首页

![image-20240203161936375](https://s2.loli.net/2024/02/03/YHXzKWRNfLvcOnC.png)

### 3、商品系统

#### 3.1、分类维护

![image-20240203162031724](https://s2.loli.net/2024/02/03/Ur7AE2utgVB6Mke.png)

![image-20240203162053912](https://s2.loli.net/2024/02/03/LpD4HPjiatx9hTI.png)

#### 3.2、品牌管理

![image-20240203162221864](https://s2.loli.net/2024/02/03/6sGw4ntMCobjyOK.png)

![image-20240203162205897](https://s2.loli.net/2024/02/03/Q4VYrPft5RDc8dO.png)

![image-20240203162244748](https://s2.loli.net/2024/02/03/BC6RPZVHKhvSEXI.png)

#### 3.3、平台属性

##### 3.3.1、属性分组

![image-20240203162505275](https://s2.loli.net/2024/02/03/y78EN4WoixmXGMw.png)

![image-20240203162528263](https://s2.loli.net/2024/02/03/9ZzGln7MEqgeupo.png)

![image-20240203162553425](https://s2.loli.net/2024/02/03/zJoaMcdNOWDQteC.png)

##### 3.3.2、规格参数

![image-20240203162706175](https://s2.loli.net/2024/02/03/VtNpwYsRLU53Q2e.png)

![image-20240203162752975](https://s2.loli.net/2024/02/03/9vXhQFebU5sEAtV.png)

##### 3.3.3、销售属性

![image-20240203162833998](https://s2.loli.net/2024/02/03/fRui6qY4pVXgZLM.png)

![image-20240203162852563](https://s2.loli.net/2024/02/03/e5EYoW7bhOydPCJ.png)

#### 3.4、商品维护

##### 3.4.1、spu管理

![image-20240203163011326](https://s2.loli.net/2024/02/03/VNCcMsv2mDFyuXQ.png)

![image-20240203163036589](https://s2.loli.net/2024/02/03/iJgIy2zG7BmSbD1.png)

##### 3.4.2、发布商品

![image-20240203163219792](https://s2.loli.net/2024/02/03/qGPYmsSfRloizT2.png)

![image-20240203163319268](https://s2.loli.net/2024/02/03/DK27CGdaoh1qXwy.png)

![image-20240203163336801](https://s2.loli.net/2024/02/03/dJr1aEVkceb6nw4.png)

![image-20240203163445525](https://s2.loli.net/2024/02/03/9fBPZzbEpTCwV57.png)

![image-20240203163503264](https://s2.loli.net/2024/02/03/VrkDdbNtnMcKp7x.png)

![image-20240203163519602](https://s2.loli.net/2024/02/03/xenByL41VfO8uh7.png)

##### 3.4.3、商品管理

![image-20240203163552416](https://s2.loli.net/2024/02/03/lJ7ufziyheFjLNv.png)

### 4、库存系统

#### 4.1、仓库维护

![image-20240203163831726](https://s2.loli.net/2024/02/03/36kejOWuriH1GBU.png)

![image-20240203163850340](https://s2.loli.net/2024/02/03/yea9kdQpLOm1Rj5.png)

#### 4.2、商品库存

![image-20240203163905016](https://s2.loli.net/2024/02/03/wTfmMqv4pVYCL7A.png)

![image-20240203163923853](https://s2.loli.net/2024/02/03/mpxQrgGU1tNTn24.png)

#### 4.3、采购单维护

##### 4.3.1、采购需求

![image-20240203163946991](https://s2.loli.net/2024/02/03/NbGL57ZJSPfX4sv.png)

![image-20240203164007180](https://s2.loli.net/2024/02/03/NagmXry9EAhMeGU.png)

![image-20240203164033516](https://s2.loli.net/2024/02/03/AZU2y9mbfaBIneR.png)

##### 4.3.2、采购单

![image-20240203164051566](https://s2.loli.net/2024/02/03/GD43LVFhrbmNs1B.png)

![image-20240203164107417](https://s2.loli.net/2024/02/03/ZCdVW3MwgSODGsv.png)



## 二、技术选型

### 1、前端技术

| 技术栈       | 版本      | 重点关注     |
| ------------ | --------- | ------------ |
| `ES6`        |           | 语法新特性   |
| `Node.js`    | `10.14.2` | 基本命令     |
| `Vue`        | `2.5.16`  | 指令、组件化 |
| `Element UI` | `2.8.2`   | 常用组件     |
| `Axios`      | `0.17.1`  | 基本请求     |

### 2、后端技术

| 技术栈                 | 版本             | 重点关注                                                     |
| ---------------------- | ---------------- | ------------------------------------------------------------ |
| `OpenJDK`              | `1.8.0_312`      |                                                              |
| `Maven`                | `3.6.3`          |                                                              |
| `Spring Boot`          | `2.2.2.RELEASE`  |                                                              |
| `Spring Cloud`         | `Hoxton.RELEASE` | `OpenFeign` 远程方法的声明式调用<br/>`GateWay` 网关          |
| `Spring Cloud Alibaba` | `2.2.0.RELEASE`  | `Nacos` 服务注册/发现、配置中心<br/>`OSS` 对象存储服务       |
| `MyBatis Plus`         | `3.3.1`          |                                                              |
| `人人开源`             | `master`         | `renren-fast` 快速开放平台<br/>`renren-fast-vue` 后台管理前端<br/>`renren-generator` 代码生成器 |

**Spring Cloud Alibaba、Spring Cloud、Spring Boot 版本选择**

参考文档：[毕业版本依赖关系(推荐使用)](https://developer.aliyun.com/article/876964)

| Spring Cloud Alibaba Version      | Spring Cloud Version                | Spring Boot Version   |
| --------------------------------- | ----------------------------------- | --------------------- |
| 2021.0.1.0                        | Spring Cloud 2021.0.1               | 2.6.3                 |
| 2.2.7.RELEASE                     | Spring Cloud Hoxton.SR12            | 2.3.12.RELEASE        |
| 2021.1                            | Spring Cloud 2020.0.1               | 2.4.2                 |
| 2.2.6.RELEASE                     | Spring Cloud Hoxton.SR9             | 2.3.2.RELEASE         |
| 2.1.4.RELEASE                     | Spring Cloud Greenwich.SR6          | 2.1.13.RELEASE        |
| 2.2.1.RELEASE                     | Spring Cloud Hoxton.SR3             | 2.2.5.RELEASE         |
| ==**2.2.0.RELEASE**==             | ==**Spring Cloud Hoxton.RELEASE**== | ==**2.2.X.RELEASE**== |
| 2.1.2.RELEASE                     | Spring Cloud Greenwich              | 2.1.X.RELEASE         |
| 2.0.4.RELEASE(停止维护，建议升级) | Spring Cloud Finchley               | 2.0.X.RELEASE         |
| 1.5.1.RELEASE(停止维护，建议升级) | Spring Cloud Edgware                | 1.5.X.RELEASE         |

### 3、数据库技术

| 技术栈  | 版本                                                     |
| ------- | -------------------------------------------------------- |
| `MySQL` | 数据库版本：`5.7`<br/>mysql-connector-java版本：`8.0.17` |
| `Redis` | `6.2.6`                                                  |

### 4、运维技术

| 技术栈    | 版本         | 重点关注 |
| --------- | ------------ | -------- |
| `Vagrant` | `2.4.1`      | 基本命令 |
| `Linux`   | `CentOS-7.1` | 常用命令 |
| `Docker`  | `25.0.1`     | 常用命令 |

### 5、开发工具

| 软件            | 版本           | 备注            |
| --------------- | -------------- | --------------- |
| `IntelliJ IDEA` | `2021.1.3`     | Java开发环境    |
| `WebStorm`      | `2021.1.3`     | 前端开发环境    |
| `DataGrip`      | `2021.1.3`     | 数据库连接工具  |
| `VS Code`       | `1.86.0`       | 多功能编辑器    |
| `Typora`        | `1.0.3(beta)`  | Markdown 编辑器 |
| `VirtualBox`    | `7.0.14`       | 虚拟机          |
| `SQLyog`        | `v10.00 Beta1` | 数据库连接工具  |
| `PowerDesigner` | `16.5`         | 数据库设计工具  |



## 三、项目结构

### 1、前端结构层次

```
renren-fast-vue
 ├── .babelrc
 ├── .editorconfig
 ├── .eslintignore
 ├── .eslintrc.js
 ├── .gitignore
 ├── .postcssrc.js
 ├── build
 ├── config
 ├── gulpfile.js
 ├── index.html
 ├── node_modules
 ├── package-lock.json
 ├── package.json
 ├── src
 │   ├── App.vue
 │   ├── assets
 │   ├── components
 │   ├── main.js
 │   ├── mock
 │   ├── router
 │   ├── store
 │   ├── utils
 │   └── views
 │       └── modules
 │           ├── common
 │           ├── job
 │           ├── member
 │           ├── oss
 │           ├── product
 │           ├── sys
 │           └── ware
 └── static
     └── config
         └── index.js
```

### 2、后端结构层次

```
gulimall
 ├── gulimall-common
 ├── gulimall-coupon
 ├── gulimall-gateway
 ├── gulimall-member
 ├── gulimall-order
 ├── gulimall-product
 ├── gulimall-third-party
 ├── gulimall-ware
 ├── renren-fast
 └── renren-generator
```



## 四、Linux 环境搭建

### 1、安装 Vagrant

```bash
# 初始化一个centos7系统
vagrant init centos7 https://mirrors.ustc.edu.cn/centos-cloud/centos/7/vagrant/x86_64/images/CentOS-7.box

# 启动虚拟机
vagrant up

# 连接虚拟机
vagrant ssh

# 使用 root 账号登录
su
vagrant

# 退出连接
exit;

# 重启虚拟机
vagrant reload

# 网卡地址
ip addr
```

### 2、修改 yum 源

```bash
# 查看可用yum源
yum repolist enabled

# 备份原 yum 源
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

# 使用新 yum 源
curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.163.com/.help/CentOS7-Base-163.repo

# 生成缓存
yum makecache
```

### 3、允许账号密码登录

```bash
vi /etc/ssh/sshd_config
# PasswordAuthentication yes

# 重启服务
service sshd restart
```

### 4、安装 Docker

```bash
# 卸载旧版本
yum remove docker \
docker-client \
docker-client-latest \
docker-common \
docker-latest \
docker-latest-logrotate \
docker-logrotate \
docker-engine

# 安装必要的依赖
yum install -y yum-utils device-mapper-persistent-data lvm2

# 设置阿里 docker 镜像仓库地址
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# 安装 docker 引擎
# 安装 Docker-CE（Community Edition，社区版）
yum -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 更新缓存
yum makecache fast

# 查看 docker 版本
docker -v

# 启动 docker
systemctl start docker
ps -ef | grep docker

# 自启动 docker
systemctl enable docker
systemctl is-enabled docker

# 配置 docker 镜像加速
mkdir -p /etc/docker
# 将JSON内容写入到 /etc/docker/daemon.json 文件中
tee /etc/docker/daemon.json <<-'EOF'
{
	"registry-mirrors": ["https://1u4widvk.mirror.aliyuncs.com"]
}
EOF
# 重新加载systemd守护进程的配置文件
systemctl daemon-reload
# 重启 docker
systemctl restart docker

# 查看镜像
docker images
```



### 5、Docker 安装 MySQL

```bash
# 拉取 MySQL 镜像
docker pull mysql:5.7

# 查看 MySQL 镜像
docker images

# 创建 MySQL 实例
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7

# 查看运行中的实例
docker ps

# 配置 MySQL
vi /mydata/mysql/conf/my.cnf
```

- `/mydata/mysql/conf/my.cnf`

```bash
# 设置客户端工具的默认字符集为utf8
[client]
default-character-set=utf8

# 设置MySQL服务器的默认字符集为utf8
[mysql]
default-character-set=utf8

[mysqld]
# 每次连接时都会将连接的字符集设置为utf8_unicode_ci
init_connect='SET collation_connection = utf8_unicode_ci'
# 每次连接时都会将连接的校对规则设置为utf8
init_connect='SET NAMES utf8'
# 指定服务器默认的字符集为utf8
character-set-server=utf8
# 指定服务器默认的校对规则为utf8_unicode_ci
collation-server=utf8_unicode_ci
# 禁用客户端和服务器之间的字符集握手
# 这意味着不会根据客户端的字符集设置来自动选择服务器的字符集，而是强制使用服务器配置的字符集。
skip-character-set-client-handshake
# 跳过域名解析
skip-name-resolve
```

- 进入 MySQL 容器实例中

```bash
# 重启 MySQL 容器实例
docker restart msyql 

# 进入 MySQL 容器实例中
docker exec -it mysql /bin/bash

# 验证 MySQL
whereis mysq

# 退出 MySQL 容器实例
exit;
```

- 通过容器的 MySQL 命令行工具连接

```bash
# 通过容器的 MySQL 命令行工具连接
docker exec -it mysql mysql -uroot -proot

# 设置 root 远程访问
# 授予用户 'root' 权限，允许其在任何主机上（'%'代表所有主机）对所有数据库（.）执行任何操作。这包括SELECT、INSERT、UPDATE、DELETE等操作。
# 同时，使用 identified by 'root' 指定了用户 'root' 的密码为 'root'。
# with grant option 表示 'root' 用户还可以将自己拥有的权限授予其他用户。
grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
# 刷新MySQL的权限表，使新的授权或权限更改立即生效，而不必重新启动MySQL服务
flush privileges;

# 退出 MySQL 客户端命令行
exit;
```

### 6、Docker 安装 Redis

```bash
# 下载镜像文件
docker pull redis

# 创建配置文件
mkdir -p /mydata/redis/conf
touch /mydata/redis/conf/redis.conf

# 创建实例并启动
docker run -p 6379:6379 --name redis \
-v /mydata/redis/data:/data \
-v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
-d redis \
redis-server /etc/redis/redis.conf

# 查看 redis 版本
docker exec -it redis redis-server -v

# 使用 redis 镜像执行 redis-cli 命令连接
docker exec -it redis redis-cli

# 默认存储在内存中，需要修改为持久化方式
vi /mydata/redis/conf/redis.conf
appendonly yes
```

### 7、Docker 容器自启动

```bash
# MySQL 容器自启动
docker update mysql --restart=always
# Redis 容器自启动
docker update redis --restart=always
```

