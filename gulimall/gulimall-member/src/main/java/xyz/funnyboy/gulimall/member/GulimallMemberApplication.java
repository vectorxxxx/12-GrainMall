package xyz.funnyboy.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/29
 */
@MapperScan("xyz.funnyboy.gulimall.member.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "xyz.funnyboy.gulimall.member.feign")
// 开启redis存储Session功能
@EnableRedisHttpSession
public class GulimallMemberApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
