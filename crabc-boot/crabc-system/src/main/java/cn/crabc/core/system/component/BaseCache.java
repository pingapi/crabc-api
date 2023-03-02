package cn.crabc.core.system.component;

import cn.crabc.core.app.constant.BaseConstant;
import cn.crabc.core.system.config.RedisConfig;
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

    public BaseCache(RedisClient redisClient, String cacheType) {
        this.redisClient = redisClient;
        this.cacheType = cacheType;
    }

    /**
     * 当前的缓存类型
     * @return
     */
    public String getCacheType() {
        return this.cacheType;
    }

    /**
     * 本地缓存api
     *
     * @param apis
     */
    public void setLocalApiCache(List<ApiInfoDTO> apis) {
        for (ApiInfoDTO api : apis) {
            apisMap.put(api.getApiMethod() + "_" + api.getApiPath(), api);
        }
    }

    /**
     * 获取缓存中API数据
     *
     * @param cacheKey
     * @return
     */
    public ApiInfoDTO getCacheApiInfo(String cacheKey) {
        ApiInfoDTO apiInfo = null;
        if (BaseConstant.REDIS_CACHE.equals(cacheType)) {
            apiInfo = redisClient.getHashMap(RedisConfig.CACHE_API_DETAIL, cacheKey);
        } else {
            apiInfo = apisMap.get(cacheKey);
        }
        return apiInfo;
    }
}
