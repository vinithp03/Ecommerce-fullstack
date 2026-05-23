package com.vinith.catalog.Config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Slf4j
@Configuration
public class CacheConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(@NonNull RuntimeException e,@NonNull Cache cache,@NonNull Object key) {
                log.warn("[CACHE] Redis GET failed for cache='{}' key='{}'. Going to DB. Cause: {}",
                        cache.getName(), key, e.getMessage());
                // not rethrowing = Spring treats as cache miss = DB called
            }

            @Override
            public void handleCachePutError(@NonNull RuntimeException e,@NonNull Cache cache,@NonNull Object key, Object value) {
                log.warn("[CACHE] Redis PUT failed for cache='{}' key='{}'. Cause: {}",
                        cache.getName(), key, e.getMessage());
                // not rethrowing = DB result still returned to caller
            }

            @Override
            public void handleCacheEvictError(@NonNull RuntimeException e,@NonNull Cache cache,@NonNull Object key) {
                log.warn("[CACHE] Redis EVICT failed for cache='{}' key='{}'. Cause: {}",
                        cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException e,@NonNull Cache cache) {
                log.warn("[CACHE] Redis CLEAR failed for cache='{}'. Cause: {}",
                        cache.getName(), e.getMessage());
            }
        };
    }
}