package io.mosip.idrepository.saltgenerator.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * @author Manoj SP
 *
 */
@Component
public class BatchJobListener extends JobExecutionListenerSupport {

	Logger mosipLogger = IdRepoLogger.getLogger(BatchJobListener.class);

	@Override
	public void beforeJob(JobExecution jobExecution) {
		mosipLogger.debug("ID_REPO_SALT_GENERATOR", "BatchJobListener", "BATCH JOB STARTED WITH STATUS : ",
				jobExecution.getStatus().name());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		mosipLogger.debug("ID_REPO_SALT_GENERATOR", "BatchJobListener", "BATCH JOB COMPLETED WITH STATUS : ",
				jobExecution.getStatus().name());
		jobExecution.setExitStatus(ExitStatus.COMPLETED);
	}
}
