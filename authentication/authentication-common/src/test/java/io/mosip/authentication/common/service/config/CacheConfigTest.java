package io.mosip.authentication.common.service.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class CacheConfigTest {

    @Test
    public void testCacheManagerBeanCreation() {
        // Load only this configuration
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(CacheConfig.class);

        ConcurrentMapCacheManager cacheManager =
                context.getBean(ConcurrentMapCacheManager.class);

        assertNotNull(cacheManager);

        context.close();
    }
}
