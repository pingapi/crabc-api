package cn.crabc.core.app.config;

import cn.crabc.core.app.filter.ApiFilter;
import cn.crabc.core.app.filter.AuthInterceptor;
import cn.crabc.core.app.filter.JwtInterceptor;
import cn.crabc.core.app.mapper.BaseUserMapper;
import cn.crabc.core.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

    private final JwtUtil jwtUtil;

    private final BaseUserMapper baseUserMapper;

    public InterceptorConfig(JwtUtil jwtUtil, BaseUserMapper baseUserMapper) {
        this.jwtUtil = jwtUtil;
        this.baseUserMapper = baseUserMapper;
    }

    @Bean
    public AuthInterceptor apiInterceptor() {
        return new AuthInterceptor();
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        // 拦截器需要回查账号状态并取服务端角色，因此依赖 JwtUtil 与用户 Mapper
        return new JwtInterceptor(jwtUtil, baseUserMapper);
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

    @Value("${crabc.api-log.response-body:false}")
    public Boolean responseBody = false;
    /**
     * 日志过滤器
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean builderRegistrationBean(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ApiFilter(responseBody));
        registration.addUrlPatterns("/api/web/*");
        registration.setName("apiFilter");
        registration.setOrder(-1);
        return registration;
    }
}
