package io.mosip.authentication.internal.service.config;

import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class BatchJobSchedulerConfig.
 * @author Loganathan Sekar
 */
@Configuration
@DependsOn("dataProcessingBatchConfig")
public class BatchJobSchedulerConfig {
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(BatchJobSchedulerConfig.class);
	
	/** The Constant CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING. */
	private static final String CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING = "1000";


	@Autowired
	private CredentialStoreJobExecutionListener listener;

	/**
	 * Schedule credential store job.
	 */
	@Bean
	public Job credentialStoreJob(JobRepository jobRepository, Step step) {
		return new JobBuilder("credentialStoreJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step)
				.end()
				.build();
	}


}
