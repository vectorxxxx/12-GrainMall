package xyz.funnyboy.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@MapperScan("xyz.funnyboy.gulimall.order.dao")
@SpringBootApplication
@EnableDiscoveryClient
// 启用 RabbitMQ
@EnableRabbit
// 开启Feign功能
@EnableFeignClients("xyz.funnyboy.gulimall.order.feign")
// 整合Redis作为session存储
@EnableRedisHttpSession
// 开启 AOP
// @EnableAspectJAutoProxy(exposeProxy = true)
public class GulimallOrderApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
