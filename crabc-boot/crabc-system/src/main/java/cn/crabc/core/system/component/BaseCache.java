package cn.crabc.core.system.component;

import cn.crabc.core.app.constant.BaseConstant;
import cn.crabc.core.system.entity.dto.ApiInfoDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 缓存数据 操作类
 *
 * @author yuqf
 */
public class BaseCache {

    private RedisClient redisClient;
    // 缓存类型，redis、local
    private String cacheType;

    // 本地缓存的api数据
    private Map<String, ApiInfoDTO> apisMap = new ConcurrentHashMap<>();

    public BaseCache() {
        this.cacheType = "local";
    }
    public BaseCache(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.cacheType ="redis";
    }

    /**
     * 当前的缓存类型
     * @return
     */
    public String getCacheType() {
        return this.cacheType;
    }

    /**
     * 缓存api
     *
     * @param apis
     */
    public void setApiCache(List<ApiInfoDTO> apis) {
        for (ApiInfoDTO api : apis) {
            if (redisClient != null) {
                redisClient.setHashMap(BaseConstant.CACHE_API_DETAIL, api.getApiMethod() + "_" + api.getApiPath(), api);
            }else{
                apisMap.put(api.getApiMethod() + "_" + api.getApiPath(), api);
            }
        }
    }

    /**
     * 获取缓存中API数据
     *
     * @param cacheKey
     * @return
     */
    public ApiInfoDTO getCacheApis(String cacheKey) {
        ApiInfoDTO apiInfo = null;
        if (BaseConstant.REDIS_CACHE.equals(cacheType) && redisClient != null) {
            apiInfo = redisClient.getHashMap(BaseConstant.CACHE_API_DETAIL, cacheKey);
        } else {
            apiInfo = apisMap.get(cacheKey);
        }
        return apiInfo;
    }
}
