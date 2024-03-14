package xyz.funnyboy.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("xyz.funnyboy.gulimall.order.dao")
@SpringBootApplication
@EnableDiscoveryClient
// 启用 RabbitMQ
@EnableRabbit
// 开启Feign功能
@EnableFeignClients("xyz.funnyboy.gulimall.order.feign")
public class GulimallOrderApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
