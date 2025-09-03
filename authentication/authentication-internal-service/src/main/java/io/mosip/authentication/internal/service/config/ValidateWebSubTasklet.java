package io.mosip.authentication.internal.service.config;

import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.batch.MissingCredentialsItemReader;
import io.mosip.authentication.internal.service.listener.InternalAuthIdChangeEventsWebSubInitializer;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.kernel.core.logger.spi.Logger;
import org.apache.http.HttpStatus;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_CHUNK_SIZE;

public class ValidateWebSubTasklet implements Tasklet {

    @Autowired
    protected ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private JobLauncher jobLauncher;

    /** The job registry. */
    @Autowired
    public JobRegistry jobRegistry;

    @Autowired
    private io.mosip.authentication.common.service.util.EnvUtil env;

    @Autowired
    private MissingCredentialsItemReader missingCredentialsItemReader;

    @Autowired
    private RetryPolicy retryPolicy;

    @Autowired
    private BackOffPolicy backOffPolicy;

    /** The credential store service. */
    @Autowired
    private CredentialStoreService credentialStoreService;

    @Autowired
    private InternalAuthIdChangeEventsWebSubInitializer idChangeWebSubInitializer;

    @Autowired
    private CredentialStoreJobExecutionListener listener;

    /** The chunk size. */
    @Value("${" + CREDENTIAL_STORE_CHUNK_SIZE + ":10}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    /** The logger. */
    private static Logger logger = IdaLogger.getLogger(ValidateWebSubTasklet.class);

    public ValidateWebSubTasklet(InternalAuthIdChangeEventsWebSubInitializer idChangeWebSubInitializer, CredentialStoreJobExecutionListener listener,
                                 JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.idChangeWebSubInitializer = idChangeWebSubInitializer;
        this.listener = listener;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Rescheduling job only when websub service is unavailable
        if (idChangeWebSubInitializer.doRegisterTopics() == HttpStatus.SC_SERVICE_UNAVAILABLE
                || idChangeWebSubInitializer.doInitSubscriptions() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
            rescheduleJob(retriggerMissingCredentials(listener, jobRepository, platformTransactionManager));
            throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
        }
        return RepeatStatus.FINISHED;
    }

    private void rescheduleJob(Job job) {
        taskScheduler.schedule(() -> {
            try {
                jobLauncher.run(job,
                        new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                     | JobParametersInvalidException e) {
                logger.warn("error in rescheduleJob - {}", e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + EnvUtil.getDelayToPullMissingCredAfterTopicSub()));
    }

    @Bean
    @Qualifier("retriggerMissingCredentials")
    public Job retriggerMissingCredentials(CredentialStoreJobExecutionListener listener, JobRepository jobRepository,
                                           PlatformTransactionManager platformTransactionManager) {
        Job job = new JobBuilder("retriggerMissingCredentials", jobRepository).incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(validateWebSubInitialization(jobRepository, platformTransactionManager)) // check if web sub subscribed to proceed
                .next(retriggerMissingCredentialsStep(jobRepository, platformTransactionManager)) // Then retrigger missing credentials
                .end().build();

        try {
            jobRegistry.register(new ReferenceJobFactory(job));
        } catch (DuplicateJobException e) {
            logger.warn("error in registering job: {}", e.getMessage());
        }
        return job;
    }

    private Step validateWebSubInitialization(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("validateWebSub", jobRepository).tasklet(
                new ValidateWebSubTasklet(idChangeWebSubInitializer, listener, jobRepository, platformTransactionManager),
                platformTransactionManager).build();
    }

    @Bean
    public Step retriggerMissingCredentialsStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(IdAuthenticationBusinessException.class, false);
        return new StepBuilder("retriggerMissingCredentialsStep", jobRepository)
                .<CredentialRequestIdsDto, Future<CredentialRequestIdsDto>>chunk(chunkSize, platformTransactionManager)
                .reader(missingCredentialsItemReader)
                .processor(asyncIdentityItemProcessor())
                .writer(asyncMissingCredentialRetriggerItemWriter())
                .faultTolerant()
                // Applying common retry policy
                .retryPolicy(retryPolicy)
                // Applying common back-off policy
                .backOffPolicy(backOffPolicy)
                .build();
    }

    @Bean
    public <T> AsyncItemProcessor<T, T> asyncIdentityItemProcessor() {
        AsyncItemProcessor<T, T> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(elem -> elem);
        asyncItemProcessor.setTaskExecutor(taskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<CredentialRequestIdsDto> asyncMissingCredentialRetriggerItemWriter() {
        AsyncItemWriter<CredentialRequestIdsDto> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(missingCredentialRetriggerItemWriter());
        return asyncItemWriter;
    }

    /**
     * Item writer.
     *
     * @return the item writer
     */
    private ItemWriter<CredentialRequestIdsDto> missingCredentialRetriggerItemWriter() {
        return credentialStoreService::processMissingCredentialRequestId;
    }

    /**
     * Task executor.
     *
     * @return the task executor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(chunkSize);
        executor.setMaxPoolSize(chunkSize);
        executor.setQueueCapacity(chunkSize);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }
}

