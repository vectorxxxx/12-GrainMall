# Seata

## 1、依赖

```xml
<!-- 分布式事务 Seata -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>
```



## 2、配置文件

`application.yml`

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver

# seata
seata:
  tx-service-group: gulimall-ware-fescar-service-group
  registry:
    type: nacos
    nacos:
      server-addr: 127.0.0.1:8848
```

`file.conf`

```nginx
service {
  #transaction service group mapping
  vgroup_mapping.gulimall-ware-fescar-service-group = "default"
  #only support when registry.type=file, please don't set multiple addresses
  default.grouplist = "127.0.0.1:8091"
  #disable seata
  disableGlobalTransaction = false
}

## transaction log store, only used in seata-server
store {
  ## store mode: file、db
  mode = "file"

  ## file store property
  file {
    ## store location dir
    dir = "sessionStore"
  }

  ## database store property
  db {
    ## the implement of javax.sql.DataSource, such as DruidDataSource(druid)/BasicDataSource(dbcp) etc.
    datasource = "dbcp"
    ## mysql/oracle/h2/oceanbase etc.
    db-type = "mysql"
    driver-class-name = "com.mysql.jdbc.Driver"
    url = "jdbc:mysql://127.0.0.1:3306/seata"
    user = "mysql"
    password = "mysql"
  }
}
```

`registry.conf`

```nginx
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    serverAddr = "localhost:8848"
    namespace = ""
    cluster = "default"
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = "0"
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    session.timeout = 6000
    connect.timeout = 2000
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "file"

  nacos {
    serverAddr = "localhost"
    namespace = ""
  }
  consul {
    serverAddr = "127.0.0.1:8500"
  }
  apollo {
    app.id = "seata-server"
    apollo.meta = "http://192.168.1.204:8801"
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    session.timeout = 6000
    connect.timeout = 2000
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "file.conf"
  }
}
```



## 3、配置类

`MySeataConfig`

```java
@Configuration
public class MySeataConfig
{
    // @Autowired
    // DataSourceProperties dataSourceProperties;
    //
    // /**
    //  * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
    //  */
    // @Bean
    // public DataSource dataSource(DataSourceProperties dataSourceProperties) {
    //     HikariDataSource dataSource = dataSourceProperties
    //             .initializeDataSourceBuilder()
    //             .type(HikariDataSource.class)
    //             .build();
    //     if (StringUtils.hasText(dataSourceProperties.getName())) {
    //         dataSource.setPoolName(dataSourceProperties.getName());
    //     }
    //     return new DataSourceProxy(dataSource);
    // }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));

        SqlSessionFactory factory = null;
        try {
            factory = bean.getObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return factory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```



## 4、注解

`WareSkuServiceImpl`

```java
@Transactional
@Override
public Boolean orderLockStock(WareSkuLockVO vo) {
    // ...
}
```

`OrderServiceImpl`

```java
// 开启 Seata 全局事务
//@GlobalTransactional
@Transactional
@Override
public OrderSubmitResponseVO submitOrder(OrderSubmitVO orderSubmitVO) {
    // ...
}
```

