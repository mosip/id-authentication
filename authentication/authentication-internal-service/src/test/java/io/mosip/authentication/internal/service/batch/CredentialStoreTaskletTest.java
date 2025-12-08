package io.mosip.authentication.internal.service.batch;
import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

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
        ReflectionTestUtils.setField(tasklet, "threadCount", 4);
        ReflectionTestUtils.setField(tasklet, "pageSize", 100);
        when(securityManager.getUser()).thenReturn("test-user");
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

        IdentityEntity identity = mock(IdentityEntity.class);

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
        List<CredentialEventStore> events = Collections.singletonList(event);
        
        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);
        when(credentialStoreService.processCredentialStoreEvent(any()))
                .thenThrow(new RuntimeException("Generic"));

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialEventRepo).saveAll(events);
    }

    @Test
    public void testExecuteWithIdAuthenticationBusinessExceptionShouldContinue() throws Exception {
        CredentialEventStore event = new CredentialEventStore();
        List<CredentialEventStore> events = Collections.singletonList(event);
        
        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);
        when(credentialStoreService.processCredentialStoreEvent(any()))
                .thenThrow(new IdAuthenticationBusinessException("TEST_ERROR", "Test error"));
        
        RepeatStatus status = tasklet.execute(contribution, chunkContext);
        
        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialEventRepo).saveAll(events);
    }

    @Test
    public void testInitShouldCreateForkJoinPool() {
        CredentialStoreTasklet newTasklet = new CredentialStoreTasklet();
        ReflectionTestUtils.setField(newTasklet, "threadCount", 5);
        ReflectionTestUtils.setField(newTasklet, "credentialEventRepo", credentialEventRepo);
        ReflectionTestUtils.setField(newTasklet, "credentialStoreService", credentialStoreService);
        ReflectionTestUtils.setField(newTasklet, "securityManager", securityManager);
        
        newTasklet.init();
        
        ForkJoinPool pool = (ForkJoinPool) ReflectionTestUtils.getField(newTasklet, "forkJoinPool");
        assertNotNull(pool);
    }
    @Test
    public void testExecuteWithExceptionShouldContinue() throws Exception {
        CredentialEventStore event = new CredentialEventStore();
        List<CredentialEventStore> events = Collections.singletonList(event);

        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);
        when(credentialStoreService.processCredentialStoreEvent(any()))
                .thenThrow(new Exception("Generic exception"));

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialEventRepo).saveAll(events);
    }

    @Test(expected = InterruptedException.class)
    public void testExecuteWithInterruptedExceptionShouldThrow() throws Exception {
        CredentialEventStore event = new CredentialEventStore();
        List<CredentialEventStore> events = Collections.singletonList(event);

        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);

        ForkJoinPool mockPool = mock(ForkJoinPool.class);
        ForkJoinTask<?> mockTask = mock(ForkJoinTask.class);

        when(mockTask.get()).thenThrow(new InterruptedException("Interrupted"));

        ReflectionTestUtils.setField(tasklet, "forkJoinPool", mockPool);

        tasklet.execute(contribution, chunkContext);
    }

    @Test
    public void testExecuteWithExecutionExceptionShouldContinue() throws Exception {
        CredentialEventStore event = new CredentialEventStore();
        List<CredentialEventStore> events = Collections.singletonList(event);

        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);

        ForkJoinPool mockPool = mock(ForkJoinPool.class);
        ForkJoinTask<?> mockTask = mock(ForkJoinTask.class);

        when(mockTask.get()).thenThrow(new ExecutionException(new RuntimeException("Execution error")));

        ReflectionTestUtils.setField(tasklet, "forkJoinPool", mockPool);

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialEventRepo).saveAll(events);
    }

    @Test
    public void testExecuteWithMultipleEventsShouldProcessAll() throws Exception {
        CredentialEventStore event1 = new CredentialEventStore();
        CredentialEventStore event2 = new CredentialEventStore();
        List<CredentialEventStore> events = Arrays.asList(event1, event2);

        IdentityEntity identity1 = mock(IdentityEntity.class);
        IdentityEntity identity2 = mock(IdentityEntity.class);

        when(credentialEventRepo.findNewOrFailedEvents(anyInt())).thenReturn(events);
        when(credentialStoreService.processCredentialStoreEvent(event1)).thenReturn(identity1);
        when(credentialStoreService.processCredentialStoreEvent(event2)).thenReturn(identity2);

        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, status);
        verify(credentialStoreService).storeIdentityEntity(identity1);
        verify(credentialStoreService).storeIdentityEntity(identity2);
        verify(credentialEventRepo).saveAll(events);
    }
}
