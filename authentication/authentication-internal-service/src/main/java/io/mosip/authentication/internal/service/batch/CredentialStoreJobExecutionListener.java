package io.mosip.authentication.internal.service.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.config.BatchConfig;
import io.mosip.kernel.core.logger.spi.Logger;

@Component
public class CredentialStoreJobExecutionListener implements JobExecutionListener {
	
	private static final Logger logger = IdaLogger.getLogger(BatchConfig.class);


	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(!jobExecution.getStepExecutions().isEmpty() && jobExecution.getStepExecutions().iterator().next().getReadCount() > 0) {
			logger.error("CredentialStoreJobExecutionListener", "afterJob", "after job execution", jobExecution);;
		}
	}

}
