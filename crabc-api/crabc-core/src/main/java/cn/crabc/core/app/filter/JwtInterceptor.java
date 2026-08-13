package cn.crabc.core.app.filter;

import cn.crabc.core.app.util.JwtUtil;
import cn.crabc.core.app.util.UserThreadLocal;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 
 * @author yuqf
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod().toUpperCase())) {
            return true;
        }
        
        // 获取并校验token
        String token = JwtUtil.getToken(request);
        if (token == null) {
            throw new CustomException(ErrorStatusEnum.JWT_LOGIN_EXPIRE.getCode(), ErrorStatusEnum.JWT_UN_AUTH.getMassage());
        }
        
        // 解析token
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            throw new CustomException(ErrorStatusEnum.JWT_LOGIN_EXPIRE.getCode(), ErrorStatusEnum.JWT_UN_AUTH.getMassage());
        }
        // 设置用户信息到线程上下文
        UserThreadLocal.set(claims);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 清除用户上下文信息
        UserThreadLocal.remove();
    }
}
