package xyz.funnyboy.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("xyz.funnyboy.gulimall.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "xyz.funnyboy.gulimall.ware.feign")
// 启用RabbitMQ
@EnableRabbit
public class GulimallWareApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
