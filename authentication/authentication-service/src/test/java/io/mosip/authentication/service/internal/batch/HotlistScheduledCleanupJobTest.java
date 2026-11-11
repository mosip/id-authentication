package io.mosip.authentication.service.internal.batch;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
@ContextConfiguration(classes ={TestContext.class, WebApplicationContext.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
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
