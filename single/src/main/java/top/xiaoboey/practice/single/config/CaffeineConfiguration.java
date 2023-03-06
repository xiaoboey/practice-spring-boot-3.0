package top.xiaoboey.practice.single.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import top.xiaoboey.practice.single.entity.SimpleLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoqb
 */
@Configuration
public class CaffeineConfiguration {
    public static final String CACHE_SIMPLE_LOG = "simple_log";
    public static final String CACHE_SIMPLE_USER = "simple_user";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.cache.cache-names}")
    private String commonCacheNames;

    @Value("${spring.cache.caffeine.spec}")
    private String commonSpec;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        if (StringUtils.hasText(commonCacheNames)) {
            Caffeine<Object, Object> caffeine = Caffeine.from(commonSpec);
            String[] cacheNames = commonCacheNames.split(",");
            for (String cacheName : cacheNames) {
                caches.add(new CaffeineCache(cacheName, caffeine.build(), true));
            }
        }

        // Custom cache
        caches.add(new CaffeineCache(CACHE_SIMPLE_LOG, Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    if (cause.equals(RemovalCause.EXPIRED) || cause.equals(RemovalCause.SIZE) || cause.equals(RemovalCause.COLLECTED)) {
                        if (value instanceof SimpleLog) {
                            SimpleLog simpleLog = (SimpleLog) value;
                            if (simpleLog.getLogTime() == null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    logger.warn("AccessLog miss: {}", objectMapper.writeValueAsString(simpleLog));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                })
                .recordStats()
                .build(), false));

        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
