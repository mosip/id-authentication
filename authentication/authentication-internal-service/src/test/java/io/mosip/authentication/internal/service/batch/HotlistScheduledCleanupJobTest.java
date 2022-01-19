package io.mosip.authentication.internal.service.batch;

import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
@ContextConfiguration(classes ={TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class HotlistScheduledCleanupJobTest {

    @InjectMocks
    private HotlistScheduledCleanupJob hotlistScheduledCleanupJob;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Mock
    private HotlistCacheRepository hotlistRepo;

    @Test
    public void cleanupUnblockedIdsTest(){
        hotlistScheduledCleanupJob.cleanupUnblockedIds();

        ReflectionTestUtils.setField(hotlistScheduledCleanupJob, "hotlistRepo", null);
        hotlistScheduledCleanupJob.cleanupUnblockedIds();
    }

    @Test
    public void cleanupExpiredIds(){
        hotlistScheduledCleanupJob.cleanupExpiredIds();

        ReflectionTestUtils.setField(hotlistScheduledCleanupJob, "hotlistRepo", null);
        hotlistScheduledCleanupJob.cleanupExpiredIds();
    }
}
