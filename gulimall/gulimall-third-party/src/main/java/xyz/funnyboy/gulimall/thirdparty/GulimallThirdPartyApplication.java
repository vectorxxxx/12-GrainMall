package xyz.funnyboy.gulimall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 10:16:48
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// 开启服务注册发现
@EnableDiscoveryClient
public class GulimallThirdPartyApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GulimallThirdPartyApplication.class, args);
    }
}
