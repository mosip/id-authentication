package io.mosip.authentication.internal.service.config;


import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.batch.MissingCredentialsItemReader;
import io.mosip.authentication.internal.service.listener.InternalAuthIdChangeEventsWebSubInitializer;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


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


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        tasklet = new ValidateWebSubTasklet(idChangeWebSubInitializer, listener, jobRepository, platformTransactionManager);
        tasklet.taskScheduler = taskScheduler;
    }

    @Test
    public void testExecuteWithWebSubAvailable() throws Exception {
        when(idChangeWebSubInitializer.doRegisterTopics()).thenReturn(HttpStatus.SC_OK);
        when(idChangeWebSubInitializer.doInitSubscriptions()).thenReturn(HttpStatus.SC_OK);

        RepeatStatus status = tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));
        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }
}
