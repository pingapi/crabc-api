package cn.crabc.core.app.filter;

import cn.crabc.core.app.util.JwtUtil;
import cn.crabc.core.app.util.UserThreadLocal;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

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
            throw new CustomException(ErrorStatusEnum.JWT_UN_AUTH.getCode(), ErrorStatusEnum.JWT_UN_AUTH.getMassage());
        }
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            throw new CustomException(ErrorStatusEnum.JWT_UN_AUTH.getCode(), ErrorStatusEnum.JWT_UN_AUTH.getMassage());
        }
        long nowTime = System.currentTimeMillis();
        Long expire = Long.parseLong(claims.get("expireTime").toString());
        long time = nowTime - expire;
        if (time/1000 > expireTime) {
            throw new CustomException(ErrorStatusEnum.JWT_LOGIN_EXPIRE.getCode(), ErrorStatusEnum.JWT_LOGIN_EXPIRE.getMassage());
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
