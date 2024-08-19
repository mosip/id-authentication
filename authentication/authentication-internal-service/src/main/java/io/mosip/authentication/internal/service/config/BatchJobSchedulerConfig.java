package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_JOB_DELAY;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_MISSING_CREDENTIAL_RETRIGGER_ENABLED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.SUBSCRIPTIONS_DELAY_ON_STARTUP;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Autowired
	@Qualifier("retriggerMissingCredentials")
	private Job retriggerMissingCredentials;
	
	/** The job launcher. */
	@Autowired
	private JobLauncher jobLauncher;
	
	@Value("${" + IDA_MISSING_CREDENTIAL_RETRIGGER_ENABLED + ":false}")
	private boolean enableMissingCredentialRetrigger;
	
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
	
	@Scheduled(initialDelayString = "#{${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000} + ${"
			+ DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION + ":60000}}", fixedDelay = Long.MAX_VALUE)
	public void retriggerMissingCredentialsJob() {
		if(enableMissingCredentialRetrigger) {
			logger.info("launching job for missing credential retriggering");
			try {
				JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
						.toJobParameters();
				jobLauncher.run(retriggerMissingCredentials, jobParameters);
			} catch (Exception e) {
				logger.error("unable to launch job for missing credential retriggering: {}", e.getMessage(), e);
			}
		} else {
			logger.info("job for missing credential retriggering is disabled");
		}
	}


}
