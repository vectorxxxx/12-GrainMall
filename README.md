## 1、安装 Vagrant

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

## 2、修改 yum 源

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

## 3、允许账号密码登录

```bash
vi /etc/ssh/sshd_config
# PasswordAuthentication yes

# 重启服务
service sshd restart
```

## 4、安装 Docker

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



## 5、Docker 安装 MySQL

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

## 6、Docker 安装 Redis

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

# 使用 redis 镜像执行 redis-cli 命令连接
docker
```

