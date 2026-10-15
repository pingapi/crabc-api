package cn.crabc.core.app.util;

import cn.crabc.core.app.entity.dto.ApiInfoDTO;
import cn.crabc.core.app.filter.AuthInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上下文 当前用户API信息
 *
 * @author yuqf
 */
public class ApiThreadLocal {
    private static final Logger log = LoggerFactory.getLogger(ApiThreadLocal.class);
    private static ThreadLocal<ApiInfoDTO> apiInfo =new ThreadLocal<>();

    /**
     * API存入上下文中
     * @param api
     */
    public static void set(ApiInfoDTO api){
        log.info("--> 设置上下文API信息，{}",api.getApiPath());
        apiInfo.set(api);
    }

    /**
     * 用户API数据
     */
    public static ApiInfoDTO get(){
        return apiInfo.get();
    }

    /**
     * 获取apiId
     */
    public static Long getApiId(){
        return apiInfo.get().getApiId();
    }

    /**
     * 清除
     */
    public static void remove(){
        apiInfo.remove();
    }
}
