package xyz.funnyboy.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("xyz.funnyboy.gulimall.order.dao")
@SpringBootApplication
@EnableDiscoveryClient
// 启用 RabbitMQ
@EnableRabbit
public class GulimallOrderApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
