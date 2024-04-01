package xyz.funnyboy.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-26 13:42:49
 */
@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") String port) {
        // 1.创建配置
        Config config = new Config();
        config
                .useSingleServer()
                .setAddress("redis://" + host + ":" + port);// 单节点模式
        // 2.创建redisson客户端实例
        return Redisson.create(config);
    }
}
