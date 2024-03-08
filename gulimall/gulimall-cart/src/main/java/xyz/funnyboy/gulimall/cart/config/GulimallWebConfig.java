package xyz.funnyboy.gulimall.cart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.funnyboy.gulimall.cart.interceptor.CartInterceptor;

/**
 * 使拦截器生效
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 10:27:10
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new CartInterceptor())
                .addPathPatterns("/**");
    }
}
