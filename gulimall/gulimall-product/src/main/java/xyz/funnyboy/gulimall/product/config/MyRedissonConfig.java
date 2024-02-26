package xyz.funnyboy.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-26 13:42:49
 */
@Configuration
public class MyRedissonConfig
{
    @Bean
    public RedissonClient redissonClient() {
        final Config config = new Config();
        config
                .useSingleServer()
                .setAddress("redis://192.168.56.10:6379");
        return Redisson.create(config);
    }
}
