package io.mosip.registration.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.manager.JobManager;
import io.mosip.registration.manager.SyncManager;

/**
 * This class gives the information of job process
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class JobProcessListener extends JobListenerSupport {

	
	/**
	 * Autowires job manager for to get Job id functionality
	 */
	@Autowired
	private JobManager jobManager;

	/**
	 * Autowires the SncTransactionManagerImpl, which Have the functionalities to
	 * get the job and to create sync transaction
	 */
	@Autowired
	private SyncManager syncTransactionManager;
	
	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(JobProcessListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.JobListener#getName()
	 */
	@Override
	public String getName() {
		return "jobProcessListener";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.listeners.JobListenerSupport#jobToBeExecuted(org.quartz.
	 * JobExecutionContext)
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Job to be executed started");

		/*
		 * ----------------JOB STARTED---------------
		 */

		// Insert SYNC Transaction
		try {
			syncTransactionManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_STARTED,

					RegistrationConstants.JOB_EXECUTION_STARTED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					jobManager.getJobId(context));
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Job to be executed ended");

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.listeners.JobListenerSupport#jobExecutionVetoed(org.quartz.
	 * JobExecutionContext)
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {

		
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Job to be rejected started");

		/*
		 * -------------------JOB REJECTED--------------
		 */

		try {

			// Insert SYNC Transaction
			syncTransactionManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_REJECTED,
					RegistrationConstants.JOB_EXECUTION_REJECTED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					jobManager.getJobId(context));
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Job to be rejected ended");

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.listeners.JobListenerSupport#jobWasExecuted(org.quartz.
	 * JobExecutionContext, org.quartz.JobExecutionException)
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Job was executed started");

	
		try {
			// Insert SYNC Transaction
			syncTransactionManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_COMPLETED,
					RegistrationConstants.JOB_EXECUTION_COMPLETED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					jobManager.getJobId(context));

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Job was executed ended");

		/*
		 * -------------------JOB EXECUTED--------------
		 */
		
	}

}
