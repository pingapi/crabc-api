package cn.crabc.core.system.filter;

import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.system.util.JwtUtil;
import cn.crabc.core.system.util.UserThreadLocal;
import io.jsonwebtoken.Claims;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 
 * @author yuqf
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if (request.getMethod().toUpperCase().equals("OPTIONS")){
            return true; // 通过OPTION请求
        }
        String token = JwtUtil.getToken(request);
        if (token == null) {
            throw new CustomException(401, "用户未登录");
        }
        Claims claims = JwtUtil.parseToken(token);
        UserThreadLocal.set(claims);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        // 清除用户上下文信息
        UserThreadLocal.remove();
    }
}
