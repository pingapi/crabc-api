package cn.crabc.core.app.filter;

import cn.crabc.core.app.entity.BaseApiLog;
import cn.crabc.core.app.entity.BaseApp;
import cn.crabc.core.app.entity.dto.ApiInfoDTO;
import cn.crabc.core.app.service.system.IBaseApiInfoService;
import cn.crabc.core.app.service.system.IBaseApiLogService;
import cn.crabc.core.app.service.system.impl.ApiRateLimitService;
import cn.crabc.core.app.util.ApiThreadLocal;
import cn.crabc.core.app.util.RequestUtils;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.app.util.SM3Util;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * API开放接口鉴权过滤 拦截器
 *
 * @author yuqf
 */

public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    // API开放接口前缀
    private static final String API_PRE = "/api/web/";
    @Autowired
    private IBaseApiLogService iBaseApiLogService;
    @Autowired
    private IBaseApiInfoService iBaseApiInfoService;
    @Value("${crabc.auth.expiresTime:10}")
    private Integer expiresTime;
    @Autowired
    private JsonMapper jsonMapper;
    @Autowired
    @Qualifier("apiCache")
    private Cache<String, ApiInfoDTO> apiCache;
    @Autowired
    private ApiRateLimitService apiRateLimitService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String apiPath = IBaseApiInfoService.normalizeApiPath(path.replace(API_PRE, ""));
        String cacheKey = IBaseApiInfoService.buildCacheKey(method, apiPath);

        ApiInfoDTO apiInfo = apiCache.getIfPresent(cacheKey);
        if (apiInfo == null) {
            apiInfo = iBaseApiInfoService.getApiInfoCache(method, apiPath);
            if (apiInfo != null) {
                apiCache.put(cacheKey, apiInfo);
            }
        }
        if (apiInfo == null) {
            setErrorResponse(request, response,ErrorStatusEnum.API_INVALID.getCode(),ErrorStatusEnum.API_INVALID.getMassage());
            return false;
        }
        // 存入当前时间，当作是日志的请求时间
        apiInfo.setRequestDate(new Date());
        apiInfo.setRequestTime(System.currentTimeMillis());
        // 放入上下文
        ApiThreadLocal.set(apiInfo);

        if (apiInfo.getEnabled() == 0) {
            setErrorResponse(request, response,ErrorStatusEnum.API_OFFLINE.getCode(),ErrorStatusEnum.API_OFFLINE.getMassage());
            return false;
        }

        if (!apiRateLimitService.tryConsume(apiInfo)) {
            setErrorResponse(request, response, ErrorStatusEnum.API_LIMIT.getCode(), ErrorStatusEnum.API_LIMIT.getMassage());
            return false;
        }

        try {
            // 应用列表
            List<BaseApp> appList = apiInfo.getAppList();

            return switch (apiInfo.getAuthType().toUpperCase()) {
                case "APP_CODE" -> checkAppCode(request,response, appList);
                case "APP_KEY" -> checkAppKey(request,response, appList);
                case "APP_SECRET" -> checkSM3(request,response, appList);
                default -> true;
            };
        }catch (Exception e) {
            setErrorResponse(request,response,ErrorStatusEnum.API_UN_AUTH.getCode(),ErrorStatusEnum.API_UN_AUTH.getMassage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        addLog(request, response, "");
        // 清除上下文
        ApiThreadLocal.remove();
    }

    /**
     * 异常返回
     * @param response
     * @param status
     * @param message
     * @throws Exception
     */
    private void setErrorResponse(HttpServletRequest request, HttpServletResponse response, int status, String message)
            throws Exception {
        // 指定错误码
        response.setStatus(400);
        response.setContentType("application/json;charset=UTF-8");
        Result result = Result.error(status, message);
        String json = jsonMapper.writeValueAsString(result);
        // 日记记录
        addLog(request, response, json);
        response.getWriter().write(json);
    }
    /**
     * 记录访问日志
     *
     * @param request
     * @param response
     */
    private void addLog(HttpServletRequest request, HttpServletResponse response, String msg) throws Exception {
        ContentCachingResponseWrapper responseWrapper = null;
        BaseApiLog apiLog = new BaseApiLog();
        long endTime = System.currentTimeMillis();
        ApiInfoDTO apiInfo = ApiThreadLocal.get();
        Date date = new Date();
        if (apiInfo != null) {
            apiLog.setApiId(apiInfo.getApiId());
            apiLog.setApiName(apiInfo.getApiName());
            apiLog.setApiMethod(apiInfo.getApiMethod());
            apiLog.setAuthType(apiInfo.getAuthType());
            apiLog.setRequestTime(apiInfo.getRequestDate());
            apiLog.setCostTime(endTime - apiInfo.getRequestTime());
        }else{
            apiLog.setRequestTime(date);
            apiLog.setApiPath(request.getRequestURI());
            apiLog.setApiMethod(request.getMethod());
        }
        int status = response.getStatus();
        apiLog.setAppName(RequestUtils.getAppKey(request));
        apiLog.setApiPath(request.getRequestURI());
        apiLog.setRequestIp(RequestUtils.getIp(request));
        apiLog.setResponseTime(date);
        apiLog.setQueryParam(request.getQueryString());
        apiLog.setResponseCode(status);
        apiLog.setRequestStatus(status == 200 ? "success" : "fail");
        try {
            if (request instanceof BaseRequestWrapper) {
                String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                apiLog.setRequestBody(requestBody);
            }
            if (response instanceof ContentCachingResponseWrapper) {
                responseWrapper = (ContentCachingResponseWrapper) response;
                byte[] content = responseWrapper.getContentAsByteArray();
                String responseBody = new String(content, StandardCharsets.UTF_8);
                apiLog.setResponseBody(responseBody);
            }
            if (status == 400) {
                apiLog.setResponseBody(msg);
            }
            iBaseApiLogService.addLog(apiLog);
        } catch (Exception e) {
            log.error("响应结果转换异常", e);
        } finally {
            if (responseWrapper != null) {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    /**
     * 验证接口访问权限APP_CODE
     *
     * @param request
     * @param appList
     * @return
     */
    private boolean checkAppCode(HttpServletRequest request,HttpServletResponse response, List<BaseApp> appList) throws Exception {
        String appCode = RequestUtils.getAppCode(request);
        if (appCode == null || appCode.isEmpty()) {
            setErrorResponse(request,response,ErrorStatusEnum.API_UN_AUTH.getCode(),ErrorStatusEnum.API_UN_AUTH.getMassage());
            return false;
        }
        boolean check = appList.stream().anyMatch(app -> app.getAppCode().equals(appCode));
        if (!check) {
            setErrorResponse(request,response,ErrorStatusEnum.API_UN_AUTH.getCode(),ErrorStatusEnum.API_UN_AUTH.getMassage());
        }
        return check;
    }
    /**
     * 验证接口访问权限APP_KEY
     *
     * @param request
     * @param appList
     * @return
     */
    private boolean checkAppKey(HttpServletRequest request, HttpServletResponse response, List<BaseApp> appList) throws Exception {
        String appKey = RequestUtils.getAppKey(request);
        if (appKey == null || appKey.isEmpty()) {
            setErrorResponse(request,response,ErrorStatusEnum.APP_UN_AUTH.getCode(),ErrorStatusEnum.APP_UN_AUTH.getMassage());
            return false;
        }
        boolean check = appList.stream().anyMatch(app -> app.getAppKey().equals(appKey));
        if (!check) {
            setErrorResponse(request,response,ErrorStatusEnum.API_AUTH_ERROR.getCode(),ErrorStatusEnum.API_AUTH_ERROR.getMassage());
        }
        return check;
    }

    /**
     * 国密签名认证
     *
     * @param request
     * @param appList
     * @return
     * @throws Exception
     */
    public boolean checkSM3(HttpServletRequest request, HttpServletResponse response, List<BaseApp> appList) throws Exception {
        // 认证参数
        String sign = Optional.ofNullable(request.getHeader("X-Sign")).orElse(request.getHeader("sign"));
        String timeStamp = Optional.ofNullable(request.getHeader("X-Timestamp")).orElse(request.getHeader("timestamp"));
        String appKey = Optional.ofNullable(request.getHeader("X-AppKey")).orElse(request.getHeader("appkey"));
        String nonce = Optional.ofNullable(request.getHeader("X-Nonce")).orElse(request.getHeader("nonce"));
        if (appKey == null || sign == null || timeStamp == null) {
            setErrorResponse(request,response,ErrorStatusEnum.SHA_PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
            return false;
        }
        // 校验时间戳,超过10分钟失效
        long authTime = Long.parseLong(timeStamp);
        long nowTime = System.currentTimeMillis() - authTime;
        if (nowTime > expiresTime * 60 * 1000) {
            setErrorResponse(request,response,ErrorStatusEnum.SHA_TIMESTAMP_EXPIRE.getCode(), ErrorStatusEnum.SHA_TIMESTAMP_EXPIRE.getMassage());
            return false;
        }

        String appSecret = appList.stream()
                .filter(app -> app.getAppKey().equals(appKey))
                .map(BaseApp::getAppSecret)
                .findFirst()
                .orElse("");


        String buildData = this.buildData(request, appKey, timeStamp, nonce);
        boolean verify = SM3Util.verify(buildData, appSecret, sign);
        if (!verify) {
            setErrorResponse(request,response,ErrorStatusEnum.API_AUTH_ERROR.getCode(),ErrorStatusEnum.API_AUTH_ERROR.getMassage());
        }
        return verify;
    }

    /**
     * 构建签名数据
     * @return
     */
    private String buildData(HttpServletRequest request, String appKey, String timestamp, String nonce) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        return method + "_" + path + "_" + appKey + "_" + timestamp + "_" + nonce;
    }
}
