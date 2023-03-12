package cn.crabc.core.admin.filter;

import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.admin.util.JwtUtil;
import cn.crabc.core.admin.util.UserThreadLocal;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 
 * @author yuqf
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Value("${crabc.token.expireTime:36000}")
    private long expireTime;

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
        if (claims == null) {
            throw new CustomException(401, "用户未登录");
        }
        long nowTime = System.currentTimeMillis();
        Long expire = Long.parseLong(claims.get("expireTime").toString());
        long time = nowTime - expire;
        if (time/1000 > expireTime) {
            throw new CustomException(401, "登录失效，请重新登录");
        }else{

        }
        UserThreadLocal.set(claims);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 清除用户上下文信息
        UserThreadLocal.remove();
    }
}
