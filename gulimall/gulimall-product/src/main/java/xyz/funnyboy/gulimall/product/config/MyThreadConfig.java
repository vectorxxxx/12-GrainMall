package xyz.funnyboy.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 14:13:15
 */
@Configuration
public class MyThreadConfig
{
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool) {
        return new ThreadPoolExecutor(
                // 核心线程数
                pool.getCoreSize(),
                // 最大线程数
                pool.getMaxSize(),
                // 存活时间
                pool.getKeepAliveTime(),
                // 时间单位
                TimeUnit.SECONDS,
                // 无界队列
                new LinkedBlockingDeque<>(100000),
                // 线程工厂
                Executors.defaultThreadFactory(),
                // 拒绝策略
                new ThreadPoolExecutor.AbortPolicy());
    }
}
