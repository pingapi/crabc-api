package cn.crabc.core.app.filter;

import cn.crabc.core.app.util.ApiThreadLocal;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

public class ApiFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ContentCachingResponseWrapper responseWrapper = null;
        try {
            // 包装类
            if (request.getContentType() == null || request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
                BaseRequestWrapper requestWrapper = new BaseRequestWrapper(request);
                responseWrapper = new ContentCachingResponseWrapper(response);
                filterChain.doFilter(requestWrapper, responseWrapper);
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }finally {
            ApiThreadLocal.remove();
            if (responseWrapper != null) {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    @Override
    public void destroy() {
        ApiThreadLocal.remove();
        Filter.super.destroy();
    }

}
