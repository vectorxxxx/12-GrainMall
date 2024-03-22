package xyz.funnyboy.gulimall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-20 17:25:06
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy",
                       exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
// 开启redis存储Session功能
@EnableRedisHttpSession
public class GulimallSeckillApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class, args);
    }
}
