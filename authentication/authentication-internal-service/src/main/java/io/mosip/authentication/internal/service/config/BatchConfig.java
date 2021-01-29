package io.mosip.authentication.internal.service.config;

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

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {
	
	private static final String CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING = "1000";

	private static Logger logger = IdaLogger.getLogger(BatchConfig.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public JobRegistry jobRegistry;

	@Value("${ida.batch.credestore.chunk.size:10}")
	private int chunkSize;

	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;
	
	@Autowired
	private CredentialStoreService credentialStoreService;
	
	@Autowired 
	private Job job;
	
	@Autowired
	private JobLauncher jobLauncher;
	
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
				.faultTolerant()
				.skip(RetryingBeforeRetryIntervalException.class)
				.build();
	}

	private ItemWriter<IdentityEntity> itemWriter() {
		return credentialStoreService::storeIdentityEntity;
	}
	
	@Bean
    public AsyncItemWriter<IdentityEntity> asyncWriter() {
        AsyncItemWriter<IdentityEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(itemWriter());
        return asyncItemWriter;
    }

	private ItemProcessor<CredentialEventStore, IdentityEntity> itemProcessor() {
		return credentialStoreService::processCredentialStoreEvent;
	}
	
	@Bean
	public AsyncItemProcessor<CredentialEventStore, IdentityEntity> asyncItemProcessor() {
		AsyncItemProcessor<CredentialEventStore, IdentityEntity> asyncItemProcessor = new AsyncItemProcessor<>();
		    asyncItemProcessor.setDelegate(itemProcessor());
		    asyncItemProcessor.setTaskExecutor(taskExecutor());
		return asyncItemProcessor;
	}


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

	@Bean
	public ItemReader<CredentialEventStore> credentialEventReader() {
		RepositoryItemReader<CredentialEventStore> reader = new RepositoryItemReader<>();
		reader.setRepository(credentialEventRepo);
		reader.setMethodName("findNewOrFailedEvents");
		final Map<String, Sort.Direction> sorts = new HashMap<>();
		    sorts.put("cr_dtimes", Direction.ASC);
		reader.setSort(sorts);
		reader.setPageSize(chunkSize);
		return reader;
	}
	
	@Scheduled(fixedDelayString = "${mosip.ida.credential.store.job.delay:" + CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING + "}")
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
