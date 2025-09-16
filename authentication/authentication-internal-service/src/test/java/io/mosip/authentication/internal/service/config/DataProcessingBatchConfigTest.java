package io.mosip.authentication.internal.service.config;

import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.batch.CredentialStoreTasklet;

@RunWith(SpringRunner.class)
public class DataProcessingBatchConfigTest {

    @Test
    public void testBeansCreated() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(DataProcessingBatchConfig.class);

        JobRegistry jobRegistry = mock(JobRegistry.class);
        CredentialStoreService credentialStoreService = mock(CredentialStoreService.class);
        CredentialStoreTasklet credentialStoreTasklet = mock(CredentialStoreTasklet.class);
        CredentialStoreJobExecutionListener listener = mock(CredentialStoreJobExecutionListener.class);
        JobRepository jobRepository = mock(JobRepository.class);
        PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);

        context.getBeanFactory().registerSingleton("jobRegistry", jobRegistry);
        context.getBeanFactory().registerSingleton("credentialStoreService", credentialStoreService);
        context.getBeanFactory().registerSingleton("credentialStoreTasklet", credentialStoreTasklet);
        context.getBeanFactory().registerSingleton("listener", listener);
        context.getBeanFactory().registerSingleton("jobRepository", jobRepository);
        context.getBeanFactory().registerSingleton("platformTransactionManager", transactionManager);

        context.refresh();

        DataProcessingBatchConfig config = context.getBean(DataProcessingBatchConfig.class);
        assertNotNull(config);

        Job credentialStoreJob = config.credentialStoreJob(listener, jobRepository, transactionManager);
        assertNotNull(credentialStoreJob);
        assertEquals("credentialStoreJob", credentialStoreJob.getName());

        Job retriggerJob = config.retriggerMissingCredentialJob(listener, jobRepository, transactionManager);
        assertNotNull(retriggerJob);
        assertEquals("retriggerMissingCredentials", retriggerJob.getName());

        Step credentialStoreStep = config.credentialStoreStep(jobRepository, transactionManager);
        assertNotNull(credentialStoreStep);
        assertEquals("credentialStoreStep", credentialStoreStep.getName());
        context.close();
    }
}
