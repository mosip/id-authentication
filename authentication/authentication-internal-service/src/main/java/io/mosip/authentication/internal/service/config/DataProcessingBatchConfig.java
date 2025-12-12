package io.mosip.authentication.internal.service.config;

import io.mosip.authentication.internal.service.listener.InternalAuthIdChangeEventsWebSubInitializer;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.internal.service.batch.MissingCredentialsItemReader;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import io.mosip.authentication.internal.service.batch.CredentialStoreTasklet;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_CHUNK_SIZE;

/**
 * The DataProcessingBatchConfig - Configuration file for scheduling Batch Job
 * for credential store, reprocessing missed credentials and reprocessing missed
 * websub messages.
 *
 * @author Loganathan Sekar
 */
@Configuration
@EnableScheduling
public class DataProcessingBatchConfig {


	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(DataProcessingBatchConfig.class);

	/** The job registry. */
	@Autowired
	public JobRegistry jobRegistry;

	/** The credential store service. */
	@Autowired
	private CredentialStoreService credentialStoreService;

	@Autowired
	private CredentialStoreTasklet credentialStoreTasklet;

    @Autowired
    private InternalAuthIdChangeEventsWebSubInitializer idChangeWebSubInitializer;

    @Autowired
    private CredentialStoreJobExecutionListener listener;

    /** The chunk size. */
    @Value("${" + CREDENTIAL_STORE_CHUNK_SIZE + ":10}")
    private int chunkSize;

    @Autowired
    private MissingCredentialsItemReader missingCredentialsItemReader;

    @Autowired
    private RetryPolicy retryPolicy;

    @Autowired
    private BackOffPolicy backOffPolicy;

	/**
	 * Credential store job.
	 *
	 * @param listener the listener
	 * @return the job
	 */
	@Bean
	@Qualifier("credentialStoreJob")
	@Scope("singleton")
	public Job credentialStoreJob(CredentialStoreJobExecutionListener listener, JobRepository jobRepository,
								  PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("credentialStoreJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(credentialStoreStep(jobRepository, platformTransactionManager))
				.end()
				.build();
	}

	@Bean
	@Qualifier("retriggerMissingCredentials")
	@Scope("singleton")
	public Job retriggerMissingCredentialJob(CredentialStoreJobExecutionListener listener, JobRepository jobRepository,
								  PlatformTransactionManager platformTransactionManager) {logger.info("inside data processing batch cofig ");
		return new JobBuilder("retriggerMissingCredentials", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(validateWebSubInitialization(jobRepository, platformTransactionManager)) // check if web sub subscribed to proceed
                .next(retriggerMissingCredentialsStep(jobRepository, platformTransactionManager)) // Then retrigger missing credentials
                .end().build();
	
	}

	/**
	 * Credential store step.
	 *
	 * @return the step
	 */
	@Bean
	public Step credentialStoreStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		return new StepBuilder("credentialStoreStep", jobRepository)
				.tasklet(credentialStoreTasklet, platformTransactionManager)
				.build();
	}
    private Step validateWebSubInitialization(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("validateWebSub", jobRepository).tasklet(
                new ValidateWebSubTasklet(idChangeWebSubInitializer, listener, jobRepository, platformTransactionManager),
                platformTransactionManager).build();
    }

    @Bean
    public Step retriggerMissingCredentialsStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        logger.info("inside data processing retriggermissingCredentialstep method ");
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

