package io.mosip.authentication.internal.service.batch;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class CredentialStoreTaskletTest {

    @InjectMocks
    private CredentialStoreTasklet tasklet;

    @Mock
    private CredentialEventStoreRepository credentialEventRepo;

    @Mock
    private IdentityCacheRepository identityCacheRepo;

    @Mock
    private CredentialStoreService credentialStoreService;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Mock
    private StepContribution contribution;

    @Mock
    private ChunkContext chunkContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Set thread count manually since @Value is not injected in test
        tasklet.threadCount = 4;
        tasklet.init();
    }
    @Test
    public void testExecute_withEmptyList_shouldNotCallSaveAll() throws Exception {
        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(Collections.emptyList());

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialEventRepo, never()).saveAll(any());
    }

    @Test
    public void testExecute_withValidEvents_shouldProcessAndSave() throws Exception {
        CredentialEventStore event = new CredentialEventStore();
        List<CredentialEventStore> events = Arrays.asList(event);

        IdentityEntity identity = mock(IdentityEntity.class); // replace with actual type

        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);
        when(credentialStoreService.processCredentialStoreEvent(any())).thenReturn(identity);

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialStoreService).storeIdentityEntity(identity);
        verify(credentialEventRepo).saveAll(events);
    }
    @Test
    public void testExecute_withGenericException_shouldContinue() throws Exception {
        CredentialEventStore event = new CredentialEventStore();
        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(Collections.singletonList(event));

        when(credentialStoreService.processCredentialStoreEvent(any()))
                .thenThrow(new RuntimeException("Generic"));

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
    }

}
