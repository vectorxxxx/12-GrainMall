// package xyz.funnyboy.gulimall.ware.config;
//
// import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
// import org.apache.ibatis.session.SqlSessionFactory;
// import org.mybatis.spring.SqlSessionTemplate;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
// import org.springframework.core.io.support.ResourcePatternResolver;
//
// import javax.sql.DataSource;
//
// /**
//  * @author VectorX
//  * @version V1.0
//  * @description
//  * @date 2024-03-16 10:35:56
//  */
// @Configuration
// public class MySeataConfig
// {
//     // @Autowired
//     // DataSourceProperties dataSourceProperties;
//     //
//     // /**
//     //  * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
//     //  */
//     // @Bean
//     // public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//     //     HikariDataSource dataSource = dataSourceProperties
//     //             .initializeDataSourceBuilder()
//     //             .type(HikariDataSource.class)
//     //             .build();
//     //     if (StringUtils.hasText(dataSourceProperties.getName())) {
//     //         dataSource.setPoolName(dataSourceProperties.getName());
//     //     }
//     //     return new DataSourceProxy(dataSource);
//     // }
//
//     @Bean(name = "sqlSessionFactory")
//     public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
//         MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
//         bean.setDataSource(dataSource);
//         ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//         bean.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));
//
//         SqlSessionFactory factory = null;
//         try {
//             factory = bean.getObject();
//         }
//         catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//         return factory;
//     }
//
//     @Bean
//     public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//         return new SqlSessionTemplate(sqlSessionFactory);
//     }
// }
