package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_CHUNK_SIZE;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RetryingBeforeRetryIntervalException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.batch.MissingCredentialsItemReader;
import io.mosip.authentication.internal.service.listener.InternalAuthIdChangeEventsWebSubInitializer;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The DataProcessingBatchConfig - Configuration file for scheduling Batch Job
 * for credential store, reprocessing missed credentials and reprocessing missed
 * websub messages.
 *
 * @author Loganathan Sekar
 */
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class DataProcessingBatchConfig {
	

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(DataProcessingBatchConfig.class);

	/** The job builder factory. */
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	/** The job registry. */
	@Autowired
	public JobRegistry jobRegistry;

	/** The chunk size. */
	@Value("${" + CREDENTIAL_STORE_CHUNK_SIZE + ":10}")
	private int chunkSize;

	/** The credential event repo. */
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;
	
	/** The credential store service. */
	@Autowired
	private CredentialStoreService credentialStoreService;
	
	@Autowired
	private MissingCredentialsItemReader missingCredentialsItemReader;
	
	@Autowired
	private RetryPolicy retryPolicy;

	@Autowired
	private BackOffPolicy backOffPolicy;
	
	@Autowired
	private InternalAuthIdChangeEventsWebSubInitializer idChangeWebSubInitializer;
	
	@Autowired
	protected ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private io.mosip.authentication.common.service.util.EnvUtil env;
	
	@Autowired
	private CredentialStoreJobExecutionListener listener;
	
	/**
	 * Credential store job.
	 *
	 * @param listener the listener
	 * @return the job
	 */
	@Bean
	@Qualifier("credentialStoreJob")
	public Job credentialStoreJob(CredentialStoreJobExecutionListener listener) {
		Job job = jobBuilderFactory.get("credentialStoreJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(credentialStoreStep())
				.end()
				.build();
		try {
			jobRegistry.register(new ReferenceJobFactory(job));
		} catch (DuplicateJobException e) {
			logger.warn("error in registering job: {}", e.getMessage());
		}
		return job;
	}
	
	@Bean
	@Qualifier("retriggerMissingCredentials")
	public Job retriggerMissingCredentials(CredentialStoreJobExecutionListener listener) {
		Job job = jobBuilderFactory.get("retriggerMissingCredentials").incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(validateWebSubInitialization()) // check if web sub subscribed to proceed
				.next(retriggerMissingCredentialsStep()) // Then retrigger missing credentials
				.end().build();
		try {
			jobRegistry.register(new ReferenceJobFactory(job));
		} catch (DuplicateJobException e) {
			logger.warn("error in registering job: {}", e.getMessage());
		}
		return job;
	}

	private TaskletStep validateWebSubInitialization() {
		return stepBuilderFactory.get("validateWebSub").tasklet((contribution, chunkContext) -> {
			// rescheduling job only when websub service is unavailable
			if (idChangeWebSubInitializer.doRegisterTopics() == HttpStatus.SC_SERVICE_UNAVAILABLE
					|| idChangeWebSubInitializer.doInitSubscriptions() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
				rescheduleJob(retriggerMissingCredentials(listener));
				throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			return null;
		}).build();
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

	/**
	 * Credential store step.
	 *
	 * @return the step
	 */
	@Bean
	public Step credentialStoreStep() {
		Map<Class<? extends Throwable>, Boolean>  exceptions = new HashMap<>();
		exceptions.put(IdAuthenticationBusinessException.class, false);
		return stepBuilderFactory.get("credentialStoreStep")
				.<CredentialEventStore, Future<IdentityEntity>>chunk(chunkSize)
				.reader(credentialEventReader())
				.processor(asyncCredentialStoreItemProcessor())
				.writer(asyncCredentialStoreItemWriter())
				// Here Job level retry is not applied, because, event level retry is handled
				// explicitly by the item processor
				.faultTolerant()
				// Skipping the processing of the event for this exception because it is thrown
				// when retry was done before the retry interval
				.skip(RetryingBeforeRetryIntervalException.class)
				.skipLimit(Integer.MAX_VALUE)
				.build();
	}
	
	@Bean
	public Step retriggerMissingCredentialsStep() {
		Map<Class<? extends Throwable>, Boolean>  exceptions = new HashMap<>();
		exceptions.put(IdAuthenticationBusinessException.class, false);
		return stepBuilderFactory.get("retriggerMissingCredentialsStep")
				.<CredentialRequestIdsDto, Future<CredentialRequestIdsDto>>chunk(chunkSize)
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
	
	/**
	 * Item writer.
	 *
	 * @return the item writer
	 */
	private ItemWriter<IdentityEntity> credentialStoreItemWriter() {
		return credentialStoreService::storeIdentityEntity;
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
	 * Async writer.
	 *
	 * @return the async item writer
	 */
	@Bean
    public AsyncItemWriter<IdentityEntity> asyncCredentialStoreItemWriter() {
        AsyncItemWriter<IdentityEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(credentialStoreItemWriter());
        return asyncItemWriter;
    }
	
	@Bean
    public AsyncItemWriter<CredentialRequestIdsDto> asyncMissingCredentialRetriggerItemWriter() {
        AsyncItemWriter<CredentialRequestIdsDto> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(missingCredentialRetriggerItemWriter());
        return asyncItemWriter;
    }
	
	/**
	 * Item processor.
	 *
	 * @return the item processor
	 */
	private ItemProcessor<CredentialEventStore, IdentityEntity> credentialStoreItemProcessor() {
		return credentialStoreService::processCredentialStoreEvent;
	}
	
	@Bean
	public <T> AsyncItemProcessor<T, T> asyncIdentityItemProcessor() {
		AsyncItemProcessor<T, T> asyncItemProcessor = new AsyncItemProcessor<>();
		    asyncItemProcessor.setDelegate(elem -> elem);
		    asyncItemProcessor.setTaskExecutor(taskExecutor());
		return asyncItemProcessor;
	}
	
	/**
	 * Async item processor.
	 *
	 * @return the async item processor
	 */
	@Bean
	public AsyncItemProcessor<CredentialEventStore, IdentityEntity> asyncCredentialStoreItemProcessor() {
		AsyncItemProcessor<CredentialEventStore, IdentityEntity> asyncItemProcessor = new AsyncItemProcessor<>();
		    asyncItemProcessor.setDelegate(credentialStoreItemProcessor());
		    asyncItemProcessor.setTaskExecutor(taskExecutor());
		return asyncItemProcessor;
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

	/**
	 * Credential event reader.
	 *
	 * @return the item reader
	 */
	@Bean
	public ItemReader<CredentialEventStore> credentialEventReader() {
		RepositoryItemReader<CredentialEventStore> reader = new RepositoryItemReader<>();
		reader.setRepository(credentialEventRepo);
		reader.setMethodName("findNewOrFailedEvents");
		final Map<String, Sort.Direction> sorts = new HashMap<>();
		    sorts.put("status_code", Direction.DESC); // NEW will be first processed than FAILED
		    sorts.put("retry_count", Direction.ASC); // then try processing Least failed entries first
		    sorts.put("cr_dtimes", Direction.ASC); // then, try processing old entries
		reader.setSort(sorts);
		reader.setPageSize(chunkSize);
		return reader;
	}
	
	
}
