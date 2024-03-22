package xyz.funnyboy.gulimall.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.funnyboy.gulimall.seckill.interceptor.LoginUserInterceptor;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 14:15:12
 */
@Configuration
public class OrderWebConfiguration implements WebMvcConfigurer
{
    @Autowired
    private LoginUserInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(interceptor)
                .addPathPatterns("/**");
    }
}
