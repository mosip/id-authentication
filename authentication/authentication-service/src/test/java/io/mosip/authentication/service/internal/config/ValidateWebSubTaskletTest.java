package io.mosip.authentication.service.internal.config;


import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.service.internal.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.service.internal.batch.MissingCredentialsItemReader;
import io.mosip.authentication.service.internal.listener.InternalAuthIdChangeEventsWebSubInitializer;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ValidateWebSubTaskletTest {
    @Mock
    private InternalAuthIdChangeEventsWebSubInitializer idChangeWebSubInitializer;
    @Mock
    private CredentialStoreJobExecutionListener listener;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private PlatformTransactionManager platformTransactionManager;
    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    private ValidateWebSubTasklet tasklet;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tasklet = new ValidateWebSubTasklet(idChangeWebSubInitializer, listener, jobRepository, platformTransactionManager);
        tasklet.taskScheduler = taskScheduler;
    }

    @Test
    public void testExecuteWithWebSubAvailable() throws Exception {
        when(idChangeWebSubInitializer.doRegisterTopics()).thenReturn(HttpStatus.SC_OK);
        when(idChangeWebSubInitializer.doInitSubscriptions()).thenReturn(HttpStatus.SC_OK);

        RepeatStatus status = tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));
        Assertions.assertEquals(RepeatStatus.FINISHED, status);
    }
}
