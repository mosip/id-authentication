package io.mosip.authentication.internal.service.config;

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
		logger.info("inside data processing batch cofig ");
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
		return new StepBuilder("credentialStoreStep", jobRepository)
				.tasklet(credentialStoreTasklet, platformTransactionManager)
				.build();
	}


}
