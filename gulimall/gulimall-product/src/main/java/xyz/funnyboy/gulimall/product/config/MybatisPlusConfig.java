package xyz.funnyboy.gulimall.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置类
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 20:15:05
 */
@EnableTransactionManagement
@MapperScan("xyz.funnyboy.gulimall.product.dao")
@Configuration
public class MybatisPlusConfig
{
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        final PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置数据库类型
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        paginationInterceptor.setOverflow(true);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
