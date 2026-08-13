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
import com.github.benmanes.caffeine.cache.Caffeine;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API开放接口鉴权过滤 拦截器
 *
 * @author yuqf
 */

public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    // API开放接口前缀
    private static final String API_PRE = "/api/web/";

    // 日志异步处理配置
    private static final int BATCH_SIZE = 500; // 每次消费200条
    private static final long CONSUME_INTERVAL_MS = 500; // 每500ms消费一次

    // 有界日志缓冲队列（超过容量则丢弃）
    private final BlockingQueue<BaseApiLog> logQueue = new LinkedBlockingQueue<>(2000);

    // 丢弃日志计数器
    private final AtomicInteger droppedLogCount = new AtomicInteger(0);

    // 定时任务调度器
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "api-log-consumer");
        thread.setDaemon(true); //
        return thread;
    });

    @Autowired
    private IBaseApiLogService iBaseApiLogService;
    @Autowired
    private IBaseApiInfoService iBaseApiInfoService;
    @Value("${crabc.auth.expiresTime:10}")
    private Integer expiresTime;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Qualifier("apiCache")
    private Cache<String, ApiInfoDTO> apiCache;
    @Autowired
    private ApiRateLimitService apiRateLimitService;

    /**
     * 构造函数：启动日志批量消费定时任务
     */
    public AuthInterceptor() {
        // 每500ms执行一次批量消费任务
        scheduler.scheduleAtFixedRate(this::consumeLogBatch, CONSUME_INTERVAL_MS, CONSUME_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 批量消费日志队列
     */
    private void consumeLogBatch() {
        try {
            List<BaseApiLog> batch = new ArrayList<>(BATCH_SIZE);
            logQueue.drainTo(batch, BATCH_SIZE);

            if (!batch.isEmpty()) {
                iBaseApiLogService.batchAddLog(batch);
                log.debug("批量插入API日志成功，数量：{}", batch.size());
            }

            // 定期报告丢弃日志数
            int dropped = droppedLogCount.getAndSet(0);
            if (dropped > 0) {
                log.warn("日志队列已满，丢弃日志数：{}", dropped);
            }
        } catch (Exception e) {
            log.error("批量消费日志失败", e);
        }
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String apiPath = IBaseApiInfoService.normalizeApiPath(path.replace(API_PRE, ""));
        String cacheKey = IBaseApiInfoService.buildCacheKey(method, apiPath);

        // 1. 获取API配置
        ApiInfoDTO apiInfo = apiCache.getIfPresent(cacheKey);
        if (apiInfo == null) {
            apiInfo = iBaseApiInfoService.getApiInfoCache(method, apiPath);
            if (apiInfo != null) {
                apiCache.put(cacheKey, apiInfo);
            }
        }

        // 2. API存在性验证
        if (apiInfo == null) {
            setErrorResponse(request, response,ErrorStatusEnum.API_INVALID.getCode(),ErrorStatusEnum.API_INVALID.getMassage());
            return false;
        }

        // 3. 存入上下文（记录请求时间用于日志和性能监控）
        apiInfo.setRequestDate(new Date());
        apiInfo.setRequestTime(System.currentTimeMillis());
        ApiThreadLocal.set(apiInfo);

        // 4. API启用状态检查
        if (apiInfo.getEnabled() == 0) {
            setErrorResponse(request, response,ErrorStatusEnum.API_OFFLINE.getCode(),ErrorStatusEnum.API_OFFLINE.getMassage());
            return false;
        }

        // 5. 限流检查（防止API被恶意频繁调用）
        if (!apiRateLimitService.tryConsume(apiInfo)) {
            setErrorResponse(request, response, ErrorStatusEnum.API_LIMIT.getCode(), ErrorStatusEnum.API_LIMIT.getMassage());
            return false;
        }

        try {
            // 6. 根据认证类型进行鉴权
            List<BaseApp> appList = apiInfo.getAppList();

            String authType = apiInfo.getAuthType().toUpperCase();
            if ("APP_CODE".equals(authType)) {
                return checkAppCode(request, response, appList);
            } else if ("APP_KEY".equals(authType)) {
                return checkAppKey(request, response, appList);
            } else if ("APP_SECRET".equals(authType)) {
                return checkSM3(request, response, appList);
            } else {
                return true; // 无认证模式
            }
        }catch (Exception e) {
            log.error("API认证异常: {}", e.getMessage(), e);
            setErrorResponse(request,response,ErrorStatusEnum.API_UN_AUTH.getCode(),ErrorStatusEnum.API_UN_AUTH.getMassage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        addLogAsync(request, response, "");
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
        String json;
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            json = "{}";
        }
        // 日记记录
        addLogAsync(request, response, json);
        response.getWriter().write(json);
    }

    /**
     * 异步记录访问日志（将日志加入队列）
     *
     * @param request
     * @param response
     */
    private void addLogAsync(HttpServletRequest request, HttpServletResponse response, String msg) {
        try {
            BaseApiLog apiLog = buildApiLog(request, response, msg);
            // 尝试将日志加入队列，如果队列已满则丢弃
            boolean offered = logQueue.offer(apiLog);
            if (!offered) {
                droppedLogCount.incrementAndGet();
            }
        } catch (Exception e) {
            log.error("构建API日志失败", e);
        }
    }

    /**
     * 构建API日志对象
     *
     * @param request
     * @param response
     * @param msg
     * @return
     */
    private BaseApiLog buildApiLog(HttpServletRequest request, HttpServletResponse response, String msg) throws Exception {
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
        } catch (Exception e) {
            log.error("响应结果转换异常", e);
        } finally {
            if (responseWrapper != null) {
                responseWrapper.copyBodyToResponse();
            }
        }
        return apiLog;
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
        // 1. 获取认证参数（支持X-前缀和无前缀两种header）
        String sign = Optional.ofNullable(request.getHeader("X-Sign")).orElse(request.getHeader("sign"));
        String timeStamp = Optional.ofNullable(request.getHeader("X-Timestamp")).orElse(request.getHeader("timestamp"));
        String appKey = Optional.ofNullable(request.getHeader("X-AppKey")).orElse(request.getHeader("appkey"));
        String nonce = Optional.ofNullable(request.getHeader("X-Nonce")).orElse(request.getHeader("nonce"));

        // 2. 必填参数检查
        if (appKey == null || sign == null || timeStamp == null) {
            setErrorResponse(request,response,ErrorStatusEnum.SHA_PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
            return false;
        }

        // 3. 时间戳有效性验证（防止过期请求）
        long authTime;
        try {
            authTime = Long.parseLong(timeStamp);
        } catch (NumberFormatException e) {
            setErrorResponse(request,response,ErrorStatusEnum.SHA_TIMESTAMP_EXPIRE.getCode(), "时间戳格式错误");
            return false;
        }

        long nowTime = System.currentTimeMillis() - authTime;
        // 检查时间戳是否在有效期内（默认10分钟）
        if (nowTime > expiresTime * 60 * 1000L || nowTime < 0) {
            setErrorResponse(request,response,ErrorStatusEnum.SHA_TIMESTAMP_EXPIRE.getCode(), ErrorStatusEnum.SHA_TIMESTAMP_EXPIRE.getMassage());
            return false;
        }


        // 4. 获取应用密钥
        String appSecret = appList.stream()
                .filter(app -> app.getAppKey().equals(appKey))
                .map(BaseApp::getAppSecret)
                .findFirst()
                .orElse("");

        if (appSecret.isEmpty()) {
            setErrorResponse(request,response,ErrorStatusEnum.API_AUTH_ERROR.getCode(),"AppKey不存在");
            return false;
        }

        // 5. 构建签名数据并验证
        String buildData = this.buildData(request, appKey, timeStamp, nonce);
        boolean verify = SM3Util.verify(buildData, appSecret, sign);

        if (!verify) {
            log.warn("签名验证失败 - AppKey: {}, IP: {}, 签名数据: {}",
                    appKey, RequestUtils.getIp(request), buildData);
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
