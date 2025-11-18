package io.mosip.authentication.internal.service.config;

import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.internal.service.batch.MissingCredentialsItemReader;
import io.mosip.authentication.internal.service.listener.InternalAuthIdChangeEventsWebSubInitializer;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import org.junit.runner.RunWith;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.batch.CredentialStoreTasklet;
import java.lang.reflect.Method;
import java.util.Collections;

@RunWith(SpringRunner.class)
public class DataProcessingBatchConfigTest {

    @Test
    public void testBeansCreated() throws Exception {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.register(DataProcessingBatchConfig.class);

    // Mock dependencies
    JobRegistry jobRegistry = mock(JobRegistry.class);
    CredentialStoreService credentialStoreService = mock(CredentialStoreService.class);
    CredentialStoreTasklet credentialStoreTasklet = mock(CredentialStoreTasklet.class);
    CredentialStoreJobExecutionListener listener = mock(CredentialStoreJobExecutionListener.class);
    JobRepository jobRepository = mock(JobRepository.class);
    PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
    MissingCredentialsItemReader reader = mock(MissingCredentialsItemReader.class);
    RetryPolicy retryPolicy = mock(RetryPolicy.class);
    BackOffPolicy backOffPolicy = mock(BackOffPolicy.class);
    InternalAuthIdChangeEventsWebSubInitializer idWebSub = mock(InternalAuthIdChangeEventsWebSubInitializer.class);

    context.getBeanFactory().registerSingleton("jobRegistry", jobRegistry);
    context.getBeanFactory().registerSingleton("credentialStoreService", credentialStoreService);
    context.getBeanFactory().registerSingleton("credentialStoreTasklet", credentialStoreTasklet);
    context.getBeanFactory().registerSingleton("listener", listener);
    context.getBeanFactory().registerSingleton("jobRepository", jobRepository);
    context.getBeanFactory().registerSingleton("platformTransactionManager", transactionManager);
    context.getBeanFactory().registerSingleton("missingCredentialsItemReader", reader);
    context.getBeanFactory().registerSingleton("retryPolicy", retryPolicy);
    context.getBeanFactory().registerSingleton("backOffPolicy", backOffPolicy);
    context.getBeanFactory().registerSingleton("idChangeWebSubInitializer", idWebSub);

    context.refresh();

    DataProcessingBatchConfig config = context.getBean(DataProcessingBatchConfig.class);
    assertNotNull(config);

    // ------------ Test Jobs -------------
    Job job1 = config.credentialStoreJob(listener, jobRepository, transactionManager);
    assertNotNull(job1);
    assertEquals("credentialStoreJob", job1.getName());

    Job job2 = config.retriggerMissingCredentialJob(listener, jobRepository, transactionManager);
    assertNotNull(job2);
    assertEquals("retriggerMissingCredentials", job2.getName());

    // ------------ Test Steps -------------
    Step step1 = config.credentialStoreStep(jobRepository, transactionManager);
    assertNotNull(step1);
    assertEquals("credentialStoreStep", step1.getName());

    Step step2 = org.springframework.test.util.ReflectionTestUtils.invokeMethod(
            config, "validateWebSubInitialization", jobRepository, transactionManager
    );
    assertNotNull(step2);

    Step step3 = config.retriggerMissingCredentialsStep(jobRepository, transactionManager);
    assertNotNull(step3);

    // ------------ Test Async Processor -------------
    AsyncItemProcessor<Object, Object> processor = config.asyncIdentityItemProcessor();
    assertNotNull(processor);
    assertNotNull(org.springframework.test.util.ReflectionTestUtils.getField(processor, "taskExecutor"));

    // ------------ Test Async Writer -------------
    AsyncItemWriter<CredentialRequestIdsDto> asyncWriter =
            config.asyncMissingCredentialRetriggerItemWriter();
    assertNotNull(asyncWriter);

    Object delegateWriter = org.springframework.test.util.ReflectionTestUtils.getField(asyncWriter, "delegate");
    assertNotNull(delegateWriter);
    assertTrue(delegateWriter instanceof ItemWriter);

    // ------------ Test private missingCredentialRetriggerItemWriter() -------------
    Method privateMethod =
            DataProcessingBatchConfig.class.getDeclaredMethod("missingCredentialRetriggerItemWriter");
    privateMethod.setAccessible(true);

    @SuppressWarnings("unchecked")
    ItemWriter<CredentialRequestIdsDto> writer =
            (ItemWriter<CredentialRequestIdsDto>) privateMethod.invoke(config);

    CredentialRequestIdsDto dto = new CredentialRequestIdsDto();

    // Create a Chunk wrapper for DTO
    org.springframework.batch.item.Chunk<CredentialRequestIdsDto> chunk =
            new org.springframework.batch.item.Chunk<>(Collections.singletonList(dto));

    // Call writer
    writer.write(chunk);

    // Verify correct chunk argument passed
    verify(credentialStoreService, times(1))
            .processMissingCredentialRequestId(argThat(argument ->
            argument.getItems().size() == 1 &&
            argument.getItems().get(0) == dto
            ));

    // ------------ Test Task Executor -------------
    TaskExecutor executor = config.taskExecutor();
    assertNotNull(executor);
    assertTrue(executor instanceof ThreadPoolTaskExecutor);

    ThreadPoolTaskExecutor tpe = (ThreadPoolTaskExecutor) executor;
    assertEquals(10, tpe.getCorePoolSize());
    assertEquals(10, tpe.getMaxPoolSize());
    assertEquals(10, tpe.getQueueCapacity());

    context.close();
    }
}
