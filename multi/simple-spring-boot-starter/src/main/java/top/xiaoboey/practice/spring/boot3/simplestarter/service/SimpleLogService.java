package top.xiaoboey.practice.spring.boot3.simplestarter.service;

import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.CaffeineConfiguration;
import top.xiaoboey.practice.spring.boot3.simplestarter.dao.SimpleLogRepository;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleLog;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * @author xiaoqb
 */
@Service
@CacheConfig(cacheNames = CaffeineConfiguration.CACHE_SIMPLE_LOG)
public class SimpleLogService {
    private final CacheManager cacheManager;
    private final SimpleLogRepository simpleLogRepository;
    private final EntityManager entityManager;

    public SimpleLogService(SimpleLogRepository simpleLogRepository,
                            CacheManager cacheManager,
                            EntityManager entityManager) {
        this.simpleLogRepository = simpleLogRepository;
        this.cacheManager = cacheManager;
        this.entityManager = entityManager;
    }

    @CacheEvict(key = "#simpleLog.getRequestId()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveThenClean(SimpleLog simpleLog) {
        Instant now = Instant.now();
        Duration duration = Duration.between(simpleLog.getAccessTime().toInstant(), now);
        simpleLog.setDurationMs(duration.toMillis());
        simpleLog.setLogTime(Timestamp.from(now));

        SimpleLog target = new SimpleLog();
        BeanUtils.copyProperties(simpleLog, target);
        simpleLogRepository.save(target);
    }

    public void saveToCache(SimpleLog simpleLog) {
        CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(CaffeineConfiguration.CACHE_SIMPLE_LOG);
        caffeineCache.put(simpleLog.getRequestId(), simpleLog);
    }

    public SimpleLog getFromCache(String requestId) {
        CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(CaffeineConfiguration.CACHE_SIMPLE_LOG);
        Cache.ValueWrapper valueWrapper = caffeineCache.get(requestId);
        if (valueWrapper == null) {
            return null;
        } else {
            return (SimpleLog) valueWrapper.get();
        }
    }

    /**
     * Lists the most recent access logs
     *
     * @param limit
     * @return
     */
    public List<SimpleLog> listRecentLogs(int limit) {
        String strSql = "select * from simple_log order by access_time desc";
        NativeQuery<SimpleLog> query = (NativeQuery<SimpleLog>) entityManager.createNativeQuery(strSql, SimpleLog.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
