package cn.crabc.core.admin.filter;

import cn.crabc.core.admin.entity.BaseApiLog;
import cn.crabc.core.admin.entity.BaseApp;
import cn.crabc.core.admin.entity.dto.ApiInfoDTO;
import cn.crabc.core.admin.enums.ApiAuthEnum;
import cn.crabc.core.admin.service.system.IBaseApiLogService;
import cn.crabc.core.admin.util.ApiThreadLocal;
import cn.crabc.core.admin.util.HmacSHAUtils;
import cn.crabc.core.admin.util.RequestUtils;
import cn.crabc.core.app.exception.CustomException;
import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.*;

/**
 * API开放接口鉴权过滤 拦截器
 *
 * @author yuqf
 */

public class AuthInterceptor implements HandlerInterceptor {
    private static Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

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
            throw new CustomException(53005, "API不存在！");
        }
        ApiInfoDTO apiInfo = (ApiInfoDTO) apiData;
        if (apiInfo.getEnabled() == 0) {
            throw new CustomException(53006, "该API已下线！");
        }
        boolean auth = true;
        if (ApiAuthEnum.CODE.getName().equalsIgnoreCase(apiInfo.getAuthType())) {
            auth = checkAppCode(request, apiInfo.getAppList() == null ? new ArrayList<>() : apiInfo.getAppList());

        } else if (ApiAuthEnum.APP_SECRET.getName().equalsIgnoreCase(apiInfo.getAuthType())) {
            auth = checkHmacSHA256(request, apiInfo.getAppList() == null ? new ArrayList<>() : apiInfo.getAppList());
        } else if (ApiAuthEnum.JWT.getName().equalsIgnoreCase(apiInfo.getAuthType())) {
            // TODO
        }
        if (auth == false) {
            throw new CustomException(53001, "您没有访问该API的权限");
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
        BaseApiLog apiLog = new BaseApiLog();
        long endTime = System.currentTimeMillis();
        ApiInfoDTO apiInfo = ApiThreadLocal.get();
        apiLog.setApiId(apiInfo.getApiId());
        apiLog.setApiName(apiInfo.getApiName());
        apiLog.setApiPath(request.getRequestURI());
        apiLog.setApiMethod(apiInfo.getApiMethod());
        apiLog.setAuthType(apiInfo.getAuthType());
        apiLog.setRequestIp(RequestUtils.getIp(request));
        apiLog.setRequestTime(apiInfo.getRequestDate());
        apiLog.setResponseTime(new Date());
        Long startTime = apiInfo.getRequestTime();
        apiLog.setCostTime(endTime - startTime);
        apiLog.setQueryParam(request.getQueryString());
        apiLog.setRequestStatus(response.getStatus() == 200 ? "success" : "fail");
        // 响应结果
        try {
            String requestBody = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            apiLog.setRequestBody(requestBody);
        } catch (Exception e) {
            log.error("响应结果转换异常", e);
        }
        iBaseApiLogService.addLog(apiLog);

    }

    /**
     * 验证接口访问权限
     *
     * @param request
     * @param appList
     * @return
     */
    private boolean checkAppCode(HttpServletRequest request, List<BaseApp> appList) {
        String appCode = RequestUtils.getAppCode(request);
        if (appCode == null || "".equals(appCode)) {
            return false;
        }
        for (BaseApp app : appList) {
            if (app.getAppCode().equals(appCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 参数签名认证
     *
     * @param request
     * @param appList
     * @return
     * @throws Exception
     */
    public boolean checkHmacSHA256(HttpServletRequest request, List<BaseApp> appList) {
        // 校验参数
        String sign = request.getHeader("sign");
        String timeStamp = request.getHeader("timestamp");
        String appKey = request.getHeader("appkey");
        if (appKey == null || sign == null || timeStamp == null) {
            throw new CustomException(53002, "认证参数不能为空！");
        }
        // 校验时间戳,超过10分钟失效
        Long authTime = Long.valueOf(timeStamp);
        long nowTime = System.currentTimeMillis() - authTime;
        if (nowTime > 10 * 60 * 1000) {
            throw new CustomException(53003, "timestamp过期！");
        }
        String method = request.getMethod();
        StringBuffer bodyStr = new StringBuffer();

        // 参数解析
        Map<String, Object> paramsMap = new HashMap<>();
        if ("POST".equals(method) || "PUT".equals(method)) {
            Map<String, Object> bodyMap = RequestUtils.getBodyMap(request);
            paramsMap.putAll(bodyMap);
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            if (values.length == 1) {
                paramsMap.put(key, values[0]);
            } else {
                paramsMap.put(key, Arrays.asList(values));
            }
        }

        ArrayList<String> keys = new ArrayList<>(paramsMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Object value = paramsMap.get(key);
            bodyStr.append(key + "=" + value.toString() + "&");
        }
        bodyStr.append("appkey=" + appKey + "&");
        bodyStr.append("timestamp=" + timeStamp);
        String appSecret = "";
        for (BaseApp app : appList) {
            if (app.getAppKey().equals(appKey)) {
                appSecret = app.getAppSecret();
            }
        }
        String signature = null;
        try {
            signature = HmacSHAUtils.HmacSHA256(bodyStr.toString(), appSecret);
        } catch (Exception e) {
            log.error("参数签名认证异常", e);
        }
        if (sign.equalsIgnoreCase(signature)) {
            return true;
        }
        return false;
    }
}
