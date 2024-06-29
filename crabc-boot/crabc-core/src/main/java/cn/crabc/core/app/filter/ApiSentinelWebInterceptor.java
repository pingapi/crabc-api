package cn.crabc.core.app.filter;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 重新限流方法
 */
@Component
@Primary
public class ApiSentinelWebInterceptor extends SentinelWebInterceptor {

    @Override
    protected void handleBlockException(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.setStatus(429);
        String errorResponse = "{\"code\": 429,\"msg\": \"接口已被限流！\", \"data\":null}";
        response.getWriter().write(errorResponse);
    }

    @Override
    protected String getResourceName(HttpServletRequest request) {
        Object resourceNameObject = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (resourceNameObject instanceof String resourceName) {
            return resourceName;
        } else {
            return null;
        }
    }

    @Override
    protected String getContextName(HttpServletRequest request) {
        return super.getContextName(request);
    }
}
