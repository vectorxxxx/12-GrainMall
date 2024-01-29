package xyz.funnyboy.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("xyz.funnyboy.gulimall.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallWareApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
