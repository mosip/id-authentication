package io.mosip.authentication.internal.service.config;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;


@RunWith(SpringRunner.class)
public class BatchJobSchedulerConfigTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job credentialStoreJob;

    @Mock
    private Job retriggerMissingCredentials;

    @InjectMocks
    private BatchJobSchedulerConfig batchJobSchedulerConfig;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testScheduleCredentialStoreJob_Success() throws Exception {
        when(jobLauncher.run(eq(credentialStoreJob), any(JobParameters.class)))
                .thenReturn(mock(JobExecution.class));

        batchJobSchedulerConfig.scheduleCredentialStoreJob();

        verify(jobLauncher, times(1)).run(eq(credentialStoreJob), any(JobParameters.class));
    }

    @Test
    public void testScheduleCredentialStoreJob_Exception() throws Exception {
        when(jobLauncher.run(eq(credentialStoreJob), any(JobParameters.class)))
                .thenThrow(new RuntimeException("some error"));

        batchJobSchedulerConfig.scheduleCredentialStoreJob();

        verify(jobLauncher, times(1)).run(eq(credentialStoreJob), any(JobParameters.class));
    }

    @Test
    public void testRetriggerMissingCredentialsJob_Enabled() throws Exception {
        java.lang.reflect.Field field = BatchJobSchedulerConfig.class.getDeclaredField("enableMissingCredentialRetrigger");
        field.setAccessible(true);
        field.set(batchJobSchedulerConfig, true);

        when(jobLauncher.run(eq(retriggerMissingCredentials), any(JobParameters.class)))
                .thenReturn(mock(JobExecution.class));

        batchJobSchedulerConfig.retriggerMissingCredentialsJob();

        verify(jobLauncher, times(1)).run(eq(retriggerMissingCredentials), any(JobParameters.class));
    }

    @Test
    public void testRetriggerMissingCredentialsJob_Disabled() throws Exception {
        java.lang.reflect.Field field = BatchJobSchedulerConfig.class.getDeclaredField("enableMissingCredentialRetrigger");
        field.setAccessible(true);
        field.set(batchJobSchedulerConfig, false);

        batchJobSchedulerConfig.retriggerMissingCredentialsJob();

        verify(jobLauncher, never()).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    public void testRetriggerMissingCredentialsJob_Exception() throws Exception {
        java.lang.reflect.Field field = BatchJobSchedulerConfig.class.getDeclaredField("enableMissingCredentialRetrigger");
        field.setAccessible(true);
        field.set(batchJobSchedulerConfig, true);

        when(jobLauncher.run(eq(retriggerMissingCredentials), any(JobParameters.class)))
                .thenThrow(new RuntimeException("retrigger error"));

        batchJobSchedulerConfig.retriggerMissingCredentialsJob();

        verify(jobLauncher, times(1)).run(eq(retriggerMissingCredentials), any(JobParameters.class));
    }
}
