package cn.crabc.core.system.filter;

import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.system.entity.BaseApiLog;
import cn.crabc.core.system.entity.BaseApp;
import cn.crabc.core.system.entity.dto.ApiInfoDTO;
import cn.crabc.core.system.enums.ApiAuthEnum;
import cn.crabc.core.system.service.system.IBaseApiLogService;
import cn.crabc.core.system.util.ApiThreadLocal;
import cn.crabc.core.system.util.RequestUtils;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * API开放接口鉴权过滤 拦截器
 *
 * @author yuqf
 */

public class ApiInterceptor implements HandlerInterceptor {
    // API开放接口前缀
    private final static String API_PRE = "/api/web/";
    @Autowired
    @Qualifier("apiCache")
    Cache<String, Object> apiCache;
    @Autowired
    private IBaseApiLogService iBaseApiLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        Object apiData = apiCache.getIfPresent(method + "_" + path.replace(API_PRE, ""));
        if (apiData == null) {
            throw new CustomException(53005, "API不存在");
        }
        ApiInfoDTO apiInfo = (ApiInfoDTO) apiData;
        if (ApiAuthEnum.CODE.getName().equalsIgnoreCase(apiInfo.getAuthType())) {
            Boolean auth = checkAppCode(request, apiInfo);
            if (auth == false) {
                throw new CustomException(53001, "您没有访问该API的权限");
            }
        } else if (ApiAuthEnum.APP_SECRET.getName().equalsIgnoreCase(apiInfo.getAuthType())) {
            // TODO
        }
        // 存入当前时间，当作是日志的请求时间
        apiInfo.setRequestDate(new Date());
        apiInfo.setRequestTime(System.currentTimeMillis());
        // 放入上下文
        ApiThreadLocal.set(apiInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        addLog(request, response, ex);
        // 清除上下文
        ApiThreadLocal.remove();
    }

    /**
     * 记录访问日志
     *
     * @param request
     * @param response
     */
    private void addLog(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        BaseApiLog log = new BaseApiLog();
        long endTime = System.currentTimeMillis();
        ApiInfoDTO apiInfo = ApiThreadLocal.get();
        log.setApiId(apiInfo.getApiId());
        log.setApiName(apiInfo.getApiName());
        log.setApiPath(apiInfo.getApiPath());
        log.setApiMethod(apiInfo.getApiMethod());
        log.setAuthType(apiInfo.getAuthType());
        log.setRequestIp(RequestUtils.getIp(request));
        log.setRequestTime(apiInfo.getRequestDate());
        log.setResponseTime(new Date());
        Long startTime = apiInfo.getRequestTime();
        log.setCostTime(endTime - startTime);
        log.setQueryParam(request.getQueryString());
        log.setRequestStatus(response.getStatus() == 200 ? "success" : "fail");
        // 响应结果
        try {
            String requestBody = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            log.setRequestBody(requestBody);
        } catch (Exception e) {

        }
        iBaseApiLogService.addLog(log);

    }

    /**
     * 验证接口访问权限
     *
     * @param request
     * @param apiInfo
     * @return
     */
    private Boolean checkAppCode(HttpServletRequest request, ApiInfoDTO apiInfo) {
        String appCode = RequestUtils.getAppCode(request);
        if (appCode == null || "".equals(appCode)) {
            return false;
        }
        List<BaseApp> appList = apiInfo.getAppList();
        if (appList == null || appList.size() == 0) {
            return false;
        }
        for (BaseApp app : appList) {
            if (app.getAppCode().equals(appCode)) {
                return true;
            }
        }
        return false;
    }

}
