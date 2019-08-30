package io.mosip.authentication.saltgenerator.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The listener interface for receiving batchJob events.
 * The class that is interested in processing a batchJob
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's addBatchJobListener method. When
 * the batchJob event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Manoj SP
 */
@Component
public class BatchJobListener extends JobExecutionListenerSupport {

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(BatchJobListener.class);

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.listener.JobExecutionListenerSupport#beforeJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {
		mosipLogger.debug("ID_REPO_SALT_GENERATOR", "BatchJobListener", "BATCH JOB STARTED WITH STATUS : ",
				jobExecution.getStatus().name());
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.listener.JobExecutionListenerSupport#afterJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void afterJob(JobExecution jobExecution) {
		mosipLogger.debug("ID_REPO_SALT_GENERATOR", "BatchJobListener", "BATCH JOB COMPLETED WITH STATUS : ",
				jobExecution.getStatus().name());
		jobExecution.setExitStatus(ExitStatus.COMPLETED);
	}
}
