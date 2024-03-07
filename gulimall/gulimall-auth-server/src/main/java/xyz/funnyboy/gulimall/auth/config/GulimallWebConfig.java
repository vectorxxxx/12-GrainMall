package xyz.funnyboy.gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 15:15:14
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer
{
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // registry
        //         .addViewController("/login.html")
        //         .setViewName("login");
        registry
                .addViewController("/reg.html")
                .setViewName("reg");
    }
}
