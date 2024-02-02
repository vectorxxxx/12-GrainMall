package xyz.funnyboy.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"xyz.funnyboy.gulimall.product.feign"})
public class GulimallProductApplication
{

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
