package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_CHUNK_SIZE;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
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
import org.springframework.transaction.PlatformTransactionManager;

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

	/** The chunk size. */
	@Value("${" + CREDENTIAL_STORE_CHUNK_SIZE + ":10}")
	private int chunkSize;

	/** The credential event repo. */
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;

	/** The credential store service. */
	@Autowired
	private CredentialStoreService credentialStoreService;



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
								  PlatformTransactionManager platformTransactionManager) {
		return new JobBuilder("retriggerMissingCredentials", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(credentialStoreStep(jobRepository, platformTransactionManager))
				.end()
				.build();
	}

	/**
	 * Credential store step.
	 *
	 * @return the step
	 */
	@Bean
	public Step credentialStoreStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		Map<Class<? extends Throwable>, Boolean>  exceptions = new HashMap<>();
		exceptions.put(IdAuthenticationBusinessException.class, false);
		return new StepBuilder("credentialStoreStep", jobRepository)
				.<CredentialEventStore, Future<IdentityEntity>>chunk(chunkSize, platformTransactionManager)
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

	/**
	 * Item writer.
	 *
	 * @return the item writer
	 */
	private ItemWriter<IdentityEntity> credentialStoreItemWriter() {
		return credentialStoreService::storeIdentityEntity;
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



	/**
	 * Item processor.
	 *
	 * @return the item processor
	 */
	private ItemProcessor<CredentialEventStore, IdentityEntity> credentialStoreItemProcessor() {
		return credentialStoreService::processCredentialStoreEvent;
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