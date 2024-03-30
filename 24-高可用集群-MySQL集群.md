## 1、Master

```bash
docker run -p 3307:3306 --name mysql-master \
-v /mydata/mysql/master/log:/var/log/mysql \
-v /mydata/mysql/master/data:/var/lib/mysql \
-v /mydata/mysql/master/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7


vi /mydata/mysql/master/conf/my.cnf
```

`/mydata/mysql/master/conf/my.cnf`

```properties
# 基本配置
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci' 
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# 主从复制配置
server_id=1
log-bin=mysql-bin
read-only=0
binlog-do-db=gulimall_ums
binlog-do-db=gulimall_pms
binlog-do-db=gulimall_oms
binlog-do-db=gulimall_sms
binlog-do-db=gulimall_wms
binlog-do-db=gulimall_admin
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=information_schema
replicate-ignore-db=performance_schema
```

保存退出

```bash
docker update mysql-master --restart=always
docker restart mysql-master

docker exec -it mysql-master mysql -uroot -proot

grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
flush privileges;

GRANT REPLICATION SLAVE ON *.* to 'backup'@'%' identified by '123456';

show master status\G;
```



## 2、Slave



```bash
docker run -p 3317:3306 --name mysql-slaver-01 \
-v /mydata/mysql/slaver/log:/var/log/mysql \
-v /mydata/mysql/slaver/data:/var/lib/mysql \
-v /mydata/mysql/slaver/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7


vi /mydata/mysql/slaver/conf/my.cnf
```

`/mydata/mysql/slaver/conf/my.cnf`

```properties
# 基本配置
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci' 
init_connect='SET NAMES utf8' 
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# 主从复制配置
server_id=2
log-bin=mysql-bin
read-only=1
binlog-do-db=gulimall_ums
binlog-do-db=gulimall_pms
binlog-do-db=gulimall_oms
binlog-do-db=gulimall_sms
binlog-do-db=gulimall_wms
binlog-do-db=gulimall_admin
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=information_schema
replicate-ignore-db=performance_schema
```

保存退出

```bash
docker update mysql-slaver-01 --restart=always
docker restart mysql-slaver-01

docker exec -it mysql-slaver-01 mysql -uroot -proot

grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
flush privileges;

# master_log_file值是根据Master节点执行show master status命令后得到的结果
change master to master_host='192.168.56.10', master_user='backup',master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=0,master_port=3307;

change master to master_host='mysql-master.gulimall', master_user='backup',master_password='123456',master_log_file='mysql-bin.000004',master_log_pos=0,master_port=3306;

start slave;

show slave status\G;
```



## 3、ShardingSphere

- Sharding-Proxy 下载：[https://archive.apache.org/dist/incubator/shardingsphere/4.0.0/apache-shardingsphere-incubating-4.0.0-sharding-proxy-bin.tar.gz](https://archive.apache.org/dist/incubator/shardingsphere/4.0.0/apache-shardingsphere-incubating-4.0.0-sharding-proxy-bin.tar.gz)

- Mysql 驱动下载：[https://cdn.mysql.com/archives/mysql-connector-java-5.1/mysql-connector-java-5.1.47.tar.gz](https://cdn.mysql.com/archives/mysql-connector-java-5.1/mysql-connector-java-5.1.47.tar.gz)

- 快速入门：[https://shardingsphere.apache.org/document/4.1.0/cn/quick-start/sharding-proxy-quick-start/](https://shardingsphere.apache.org/document/4.1.0/cn/quick-start/sharding-proxy-quick-start/)

- 配置手册：[https://shardingsphere.apache.org/document/4.1.0/cn/manual/sharding-proxy/configuration/](https://shardingsphere.apache.org/document/4.1.0/cn/manual/sharding-proxy/configuration/)

`server.yaml`

```yaml
authentication:
  users:
    root:
      password: root
    sharding:
      password: sharding
      authorizedSchemas: sharding_db
props:
  acceptor.size: 16
  sql.show: true
```

`config-sharding.yaml`

```yaml
schemaName: sharding_db

dataSources:
  ds_0:
    url: jdbc:mysql://192.168.56.10:3307/demo_ds_0?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50
  ds_1:
    url: jdbc:mysql://192.168.56.10:3307/demo_ds_1?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50

shardingRule:
  tables:
    t_order:
      actualDataNodes: ds_${0..1}.t_order_${0..1}
      tableStrategy:
        inline:
          shardingColumn: order_id
          algorithmExpression: t_order_${order_id % 2}
      keyGenerator:
        type: SNOWFLAKE
        column: order_id
    t_order_item:
      actualDataNodes: ds_${0..1}.t_order_item_${0..1}
      tableStrategy:
        inline:
          shardingColumn: order_id
          algorithmExpression: t_order_item_${order_id % 2}
      keyGenerator:
        type: SNOWFLAKE
        column: order_item_id
  bindingTables:
    - t_order,t_order_item
  defaultDatabaseStrategy:
    inline:
      shardingColumn: user_id
      algorithmExpression: ds_${user_id % 2}
  defaultTableStrategy:
    none:
```

`config-master_slave_0.yaml`

```yaml
schemaName: sharding_db_0

dataSources:
  master_ds_0:
    url: jdbc:mysql://192.168.56.10:3307/demo_ds_0?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50
  slave_ds_0:
    url: jdbc:mysql://192.168.56.10:3317/demo_ds_0?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50

masterSlaveRule:
  name: ms_ds
  masterDataSourceName: master_ds_0
  slaveDataSourceNames:
    - slave_ds_0
  loadBalanceAlgorithmType: ROUND_ROBIN
```

`config-master_slave_1.yaml`

```yaml
schemaName: sharding_db_1

dataSources:
  master_ds_1:
    url: jdbc:mysql://192.168.56.10:3307/demo_ds_1?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50
  slave_ds_1:
    url: jdbc:mysql://192.168.56.10:3317/demo_ds_1?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50

masterSlaveRule:
  name: ms_ds
  masterDataSourceName: master_ds_1
  slaveDataSourceNames:
    - slave_ds_1
  loadBalanceAlgorithmType: ROUND_ROBIN
```

通过命令行 `start.bat 3388` 启动，端口号可以自己指定

再通过数据库连接工具进行连接

<img src="C:\Users\uxiah\AppData\Roaming\Typora\typora-user-images\image-20240328234806604.png" alt="image-20240328234806604" style="zoom: 80%;" />

执行建表语句

```bash
CREATE TABLE `t_order` (
`order_id` bigint(20) NOT NULL,
`user_id` int(11) NOT NULL,
`status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
PRIMARY KEY (`order_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;


CREATE TABLE `t_order_item` (
`order_item_id` bigint(20) NOT NULL,
`order_id` bigint(20) NOT NULL,
`user_id` int(11) NOT NULL,
`content` varchar(255) COLLATE utf8_bin DEFAULT NULL,
`status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
PRIMARY KEY (`order_item_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;
```

就会发现

<img src="https://s2.loli.net/2024/03/28/NPIr4dOD6slnZB9.png" alt="image-20240328235338619" style="zoom: 150%;" />

<img src="https://s2.loli.net/2024/03/30/DzQe5oF1aJ9ldvU.png" alt="image-20240328235400895" style="zoom:150%;" />

