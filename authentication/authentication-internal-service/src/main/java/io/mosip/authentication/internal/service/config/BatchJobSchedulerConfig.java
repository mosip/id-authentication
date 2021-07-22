package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_JOB_DELAY;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.SUBSCRIPTIONS_DELAY_ON_STARTUP;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;

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
	
	/** The credential store job. */
	@Autowired
	@Qualifier("credentialStoreJob")
	private Job credentialStoreJob;
	
	/** The retrigger missing credential issuances job. */
	@Autowired
	@Qualifier("pullFailedWebsubMessagesAndProcess")
	private Job pullFailedWebsubMessagesAndProcess;
	
	@Autowired
	@Qualifier("retriggerMissingCredentials")
	private Job retriggerMissingCredentials;
	
	/** The job launcher. */
	@Autowired
	private JobLauncher jobLauncher;
	
	/**
	 * Schedule credential store job.
	 */
	@Scheduled(fixedDelayString = "${" + CREDENTIAL_STORE_JOB_DELAY + ":" + CREDENTIAL_STORE_DEFAULT_DELAY_MILLISECS_STRING + "}")
	public void scheduleCredentialStoreJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(credentialStoreJob, jobParameters);
		} catch (Exception e) {
			logger.error("unable to launch job for credential store batch: {}", e.getMessage(), e);
		}
	}
	
	/**
	 * Schedule missing credential retrigger job.
	 * Scheduling to run only once in the startup.
	 */
	// Waiting for one more minute for the subscription to complete for credential event topic.
	@Scheduled(initialDelayString = "#{${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000}}", fixedDelay = Long.MAX_VALUE)
	public void scheduleFailedMessagesProcessJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(pullFailedWebsubMessagesAndProcess, jobParameters);
		} catch (Exception e) {
			logger.error("unable to launch job pullFailedWebsubMessagesAndProcess: {}", e.getMessage(), e);
		}
	}
	
	@Scheduled(initialDelayString = "#{${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000} + ${"
			+ DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION + ":60000}}", fixedDelay = Long.MAX_VALUE)
	public void retriggerMissingCredentialsJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(retriggerMissingCredentials, jobParameters);
		} catch (Exception e) {
			logger.error("unable to launch job for credential store batch: {}", e.getMessage(), e);
		}
	}


}
