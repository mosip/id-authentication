package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_CHUNK_SIZE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_JOB_DELAY;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.impl.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RetryingBeforeRetryIntervalException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The CredentialStoreBatchConfig - Configuration file for scheduling Batch Job
 * for credential store.
 *
 * @author Loganathan Sekar
 */
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class CredentialStoreBatchConfig {
	
	/** The Constant CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING. */
	private static final String CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING = "1000";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(CredentialStoreBatchConfig.class);

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
	
	/** The job. */
	@Autowired 
	private Job job;
	
	/** The job launcher. */
	@Autowired
	private JobLauncher jobLauncher;
	
	/**
	 * Credential store job.
	 *
	 * @param listener the listener
	 * @return the job
	 */
	@Bean
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

	/**
	 * Credential store step.
	 *
	 * @return the step
	 */
	@Bean
	public Step credentialStoreStep() {
		Map<Class<? extends Throwable>, Boolean>  exceptions = new HashMap<>();
		exceptions.put(IdAuthenticationBusinessException.class, false);
		RepositoryItemWriter<CredentialEventStore> writer = new RepositoryItemWriter<>();
		writer.setRepository(credentialEventRepo);
		writer.setMethodName("update");
		
		return stepBuilderFactory.get("credentialStoreStep")
				.<CredentialEventStore, Future<IdentityEntity>>chunk(chunkSize)
				.reader(credentialEventReader())
				.processor(asyncItemProcessor())
				.writer(asyncWriter())
				// Here Job level retry is not applied, because, event level retry is handled
				// explicitly by the item processor
				.faultTolerant()
				// Skipping the processing of the event for this exception because it is thrown
				// when try was tried before the retry interval
				.skip(RetryingBeforeRetryIntervalException.class)
				.skipLimit(Integer.MAX_VALUE)
				.build();
	}

	/**
	 * Item writer.
	 *
	 * @return the item writer
	 */
	private ItemWriter<IdentityEntity> itemWriter() {
		return credentialStoreService::storeIdentityEntity;
	}
	
	/**
	 * Async writer.
	 *
	 * @return the async item writer
	 */
	@Bean
    public AsyncItemWriter<IdentityEntity> asyncWriter() {
        AsyncItemWriter<IdentityEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(itemWriter());
        return asyncItemWriter;
    }

	/**
	 * Item processor.
	 *
	 * @return the item processor
	 */
	private ItemProcessor<CredentialEventStore, IdentityEntity> itemProcessor() {
		return credentialStoreService::processCredentialStoreEvent;
	}
	
	/**
	 * Async item processor.
	 *
	 * @return the async item processor
	 */
	@Bean
	public AsyncItemProcessor<CredentialEventStore, IdentityEntity> asyncItemProcessor() {
		AsyncItemProcessor<CredentialEventStore, IdentityEntity> asyncItemProcessor = new AsyncItemProcessor<>();
		    asyncItemProcessor.setDelegate(itemProcessor());
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
	
	/**
	 * Schedule job.
	 */
	@Scheduled(fixedDelayString = "${" + CREDENTIAL_STORE_JOB_DELAY + ":" + CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING + "}")
	public void scheduleJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(job, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
