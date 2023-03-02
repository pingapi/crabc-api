package cn.crabc.core.system.util;

import cn.crabc.core.system.entity.dto.ApiInfoDTO;

/**
 * 上下文 当前用户API信息
 *
 * @author yuqf
 */
public class ApiThreadLocal {

    private static ThreadLocal<ApiInfoDTO> apiInfo =new ThreadLocal<>();

    /**
     * API存入上下文中
     * @param api
     */
    public static void set(ApiInfoDTO api){
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
