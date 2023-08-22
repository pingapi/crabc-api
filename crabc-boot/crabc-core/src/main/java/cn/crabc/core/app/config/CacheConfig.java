package cn.crabc.core.app.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * 临时缓存对象(5分钟)
     * @return
     */
    @Bean("dataCache")
    public Cache<String, Object> dataCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 缓存API对象 10小时
     * @return
     */
    @Bean("apiCache")
    public Cache<String, Object> apiCaffeine() {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.HOURS)
                .build();
    }


    /**
     * 元数据配置缓存管理器
     *
     * @return
     */
    @Bean("metaDataManager")
    public CacheManager metaDataCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(1800, TimeUnit.SECONDS);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

}
