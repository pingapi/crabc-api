package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.dto.ApiInfoDTO;
import cn.crabc.core.app.service.system.IBaseApiInfoService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * API限流服务，使用Bucket4j根据Method+URL维度控制发布接口请求频率。
 */
@Service
public class ApiRateLimitService {

    private static final String KEY_PREFIX = "rate_limit:";
    private static final long MIN_CACHE_SECONDS = 300L;

    private final Cache<String, BucketEntry> bucketCache;

    public ApiRateLimitService() {
        this.bucketCache = Caffeine.newBuilder()
                .expireAfter(new Expiry<String, BucketEntry>() {
                    /**
                     * Bucket缓存生命周期跟随限流窗口，窗口越长缓存越久，同时保留最小5分钟兜底避免短窗口频繁重建。
                     */
                    @Override
                    public long expireAfterCreate(String key, BucketEntry value, long currentTime) {
                        return TimeUnit.SECONDS.toNanos(value.cacheSeconds());
                    }

                    /**
                     * 配置变化后put新Bucket，重新按新窗口计算过期时间。
                     */
                    @Override
                    public long expireAfterUpdate(String key, BucketEntry value, long currentTime, long currentDuration) {
                        return TimeUnit.SECONDS.toNanos(value.cacheSeconds());
                    }

                    /**
                     * 读取不延长过期时间，避免低频接口的历史Bucket长期留在本地内存。
                     */
                    @Override
                    public long expireAfterRead(String key, BucketEntry value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }

    /**
     * 消费一次请求令牌；未配置限流时直接放行，配置变化时重建Bucket。
     */
    public boolean tryConsume(ApiInfoDTO apiInfo) {
        if (!isRateLimitEnabled(apiInfo)) {
            return true;
        }
        String key = buildKey(apiInfo.getApiMethod(), apiInfo.getApiPath());
        BucketEntry entry = bucketCache.get(key, ignore -> buildEntry(apiInfo));
        if (!entry.sameConfig(apiInfo)) {
            entry = buildEntry(apiInfo);
            bucketCache.put(key, entry);
        }
        return entry.bucket.tryConsume(1);
    }

    /**
     * 限流配置保存或清空后删除运行时Bucket，确保下一次请求按新配置创建。
     */
    public void invalidate(String method, String apiPath) {
        bucketCache.invalidate(buildKey(method, apiPath));
    }

    /**
     * 限流Key复用API缓存的Method+URL规范化规则，避免同一路径出现多个计数维度。
     */
    public String buildKey(String method, String apiPath) {
        return KEY_PREFIX + IBaseApiInfoService.buildCacheKey(method, apiPath);
    }

    /**
     * 只有窗口秒数和请求次数都为正数时才启用限流。
     */
    private boolean isRateLimitEnabled(ApiInfoDTO apiInfo) {
        return apiInfo != null
                && apiInfo.getRateLimitWindowSeconds() != null
                && apiInfo.getRateLimitWindowSeconds() > 0
                && apiInfo.getRateLimitCount() != null
                && apiInfo.getRateLimitCount() > 0;
    }

    /**
     * Bucket使用按窗口周期重置的补充策略，符合“1分钟5次”这类固定窗口语义。
     */
    private BucketEntry buildEntry(ApiInfoDTO apiInfo) {
        long capacity = apiInfo.getRateLimitCount();
        Duration window = Duration.ofSeconds(apiInfo.getRateLimitWindowSeconds());
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(capacity, window));
        Bucket bucket = Bucket.builder().addLimit(limit).build();
        return new BucketEntry(bucket, apiInfo.getRateLimitWindowSeconds(), apiInfo.getRateLimitCount());
    }

    /**
     * Bucket与创建时配置绑定，配置变更时通过快照判断是否需要重建。
     */
    private static class BucketEntry {
        private final Bucket bucket;
        private final Integer windowSeconds;
        private final Integer limitCount;

        private BucketEntry(Bucket bucket, Integer windowSeconds, Integer limitCount) {
            this.bucket = bucket;
            this.windowSeconds = windowSeconds;
            this.limitCount = limitCount;
        }

        private boolean sameConfig(ApiInfoDTO apiInfo) {
            return apiInfo != null
                    && windowSeconds.equals(apiInfo.getRateLimitWindowSeconds())
                    && limitCount.equals(apiInfo.getRateLimitCount());
        }

        /**
         * 缓存过期按窗口两倍计算，低于5分钟时使用5分钟兜底，兼顾短窗口复用和长期内存回收。
         */
        private long cacheSeconds() {
            return Math.max(windowSeconds.longValue() * 2, MIN_CACHE_SECONDS);
        }
    }
}
