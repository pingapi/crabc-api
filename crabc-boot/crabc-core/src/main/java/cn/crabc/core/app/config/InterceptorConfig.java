package cn.crabc.core.app.config;

import cn.crabc.core.app.filter.ApiFilter;
import cn.crabc.core.app.filter.AuthInterceptor;
import cn.crabc.core.app.filter.JwtInterceptor;
import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

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
                .excludePathPatterns("/api/box/sys/user/login", "/api/box/sys/user/loginout", "/api/box/sys/user/register"); // 不拦截的请求

        // API开放接口拦截器
        registry.addInterceptor(apiInterceptor())
                .addPathPatterns("/api/web/**"); // 需要拦截的请求
    }

    /**
     * 日志过滤器
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean builderRegistrationBean(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ApiFilter());
        registration.addUrlPatterns("/api/web/*");
        registration.setName("apiFilter");
        registration.setOrder(-1);
        return registration;
    }

    /**
     * 限流过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/api/web/*");
        registration.setName("sentinelFilter");
        registration.setOrder(1);
        return registration;
    }
}
