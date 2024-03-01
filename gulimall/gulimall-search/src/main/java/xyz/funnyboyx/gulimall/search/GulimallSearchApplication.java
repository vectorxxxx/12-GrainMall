package xyz.funnyboyx.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-05 22:24:46
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
// 开启服务注册发现
@EnableDiscoveryClient
// 开启OpenFeign
@EnableFeignClients
public class GulimallSearchApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GulimallSearchApplication.class, args);
    }
}
