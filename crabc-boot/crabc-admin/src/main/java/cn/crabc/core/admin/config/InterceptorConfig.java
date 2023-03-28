package cn.crabc.core.admin.config;

import cn.crabc.core.admin.filter.AuthInterceptor;
import cn.crabc.core.admin.filter.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器
 *
 * @author yuqf
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public AuthInterceptor apiInterceptor() {
        return new AuthInterceptor();
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/api/box/**") // 需要拦截的请求
                .excludePathPatterns("/api/box/sys/user/login", "/api/box/sys/user/loginout"); // 不拦截的请求

        // API开放接口拦截器
        registry.addInterceptor(apiInterceptor())
                .addPathPatterns("/api/web/**"); // 需要拦截的请求
    }
}
