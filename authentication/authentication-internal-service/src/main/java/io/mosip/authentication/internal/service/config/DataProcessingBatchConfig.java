package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_CHUNK_SIZE;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.FailedMessageEntity;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.FailedMessagesRepo;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RetryingBeforeRetryIntervalException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.batch.FailedWebsubMessageProcessor;
import io.mosip.authentication.internal.service.batch.FailedWebsubMessagesReader;
import io.mosip.authentication.internal.service.batch.MissingCredentialsItemReader;
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
	
	@Autowired
	private FailedMessagesRepo failedMessagesRepo;
	
	/** The credential store service. */
	@Autowired
	private CredentialStoreService credentialStoreService;
	
	@Autowired
	private MissingCredentialsItemReader missingCredentialsItemReader;
	
	@Autowired
	private FailedWebsubMessagesReader failedWebsubMessagesReader;
	
	@Autowired
	private FailedWebsubMessageProcessor failedWebsubMessageProcessor;
	
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
			logger.warn("error in registering job: {}", e.getMessage(), e);
		}
		return job;
	}
	
	@Bean
	@Qualifier("pullFailedWebsubMessagesAndProcess")
	public Job pullFailedWebsubMessagesAndProcess(CredentialStoreJobExecutionListener listener) {
		Job job = jobBuilderFactory.get("pullFailedWebsubMessagesAndProcess")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(pullFailedMessagesAndStore()) // First pull and store failed websub messages
				.next(processFailedMessagesAndProcess()) // Then process failed websub messages
				.next(retriggerMissingCredentialsStep()) // Then retrigger missing credentials
				.end()
				.build();
		try {
			jobRegistry.register(new ReferenceJobFactory(job));
		} catch (DuplicateJobException e) {
			logger.warn("error in registering job: {}", e.getMessage(), e);
		}
		return job;
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
	
	@Bean
	public Step pullFailedMessagesAndStore() {
		Map<Class<? extends Throwable>, Boolean>  exceptions = new HashMap<>();
		exceptions.put(IdAuthenticationBusinessException.class, false);
		return stepBuilderFactory.get("pullFailedMessagesAndStore")
				.<FailedMessage, Future<FailedMessage>>chunk(chunkSize)
				.reader(failedWebsubMessagesReader)
				.processor(asyncIdentityItemProcessor())
				.writer(asyncFailedMessagesHandlerItemWriter())
				.faultTolerant()
				// Applying common retry policy
				.retryPolicy(retryPolicy)
				// Applying common back-off policy
				.backOffPolicy(backOffPolicy)
				.build();
	}
	
	@Bean
	public Step processFailedMessagesAndProcess() {
		Map<Class<? extends Throwable>, Boolean>  exceptions = new HashMap<>();
		exceptions.put(IdAuthenticationBusinessException.class, false);
		return stepBuilderFactory.get("processFailedMessagesAndProcess")
				.<FailedMessageEntity, Future<FailedMessageEntity>>chunk(chunkSize)
				.reader(failedMessageReader())
				.processor(asyncIdentityItemProcessor())
				.writer(asyncFailedMessagesEntityItemWriter())
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
	
	private ItemWriter<FailedMessage> failedMessagesHandlerItemWriter() {
		return failedWebsubMessageProcessor::storeFailedWebsubMessages;
	}
	
	private ItemWriter<FailedMessageEntity> failedMessagesEntityItemWriter() {
		return failedWebsubMessageProcessor::processFailedWebsubMessages;
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
	
	@Bean
    public AsyncItemWriter<FailedMessage> asyncFailedMessagesHandlerItemWriter() {
        AsyncItemWriter<FailedMessage> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(failedMessagesHandlerItemWriter());
        return asyncItemWriter;
    }
	
	@Bean
    public AsyncItemWriter<FailedMessageEntity> asyncFailedMessagesEntityItemWriter() {
        AsyncItemWriter<FailedMessageEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(failedMessagesEntityItemWriter());
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
	
	@Bean
	public ItemReader<FailedMessageEntity> failedMessageReader() {
		RepositoryItemReader<FailedMessageEntity> reader = new RepositoryItemReader<>();
		reader.setRepository(failedMessagesRepo);
		reader.setMethodName("findNewFailedMessages");
		final Map<String, Sort.Direction> sorts = new HashMap<>();
		    sorts.put("cr_dtimes", Direction.ASC); 
		reader.setSort(sorts);
		reader.setPageSize(chunkSize);
		return reader;
	}
	
	
}
