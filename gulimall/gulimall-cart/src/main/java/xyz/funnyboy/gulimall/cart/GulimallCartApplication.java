package xyz.funnyboy.gulimall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 09:07:04
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// 开启服务注册发现
@EnableDiscoveryClient
// 开启Feign客户端
@EnableFeignClients("xyz.funnyboy.gulimall.cart.feign")
public class GulimallCartApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GulimallCartApplication.class, args);
    }
}
