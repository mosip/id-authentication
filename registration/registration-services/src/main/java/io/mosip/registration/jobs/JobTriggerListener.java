package io.mosip.registration.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * This class gives the information of job trigger {@link Trigger} such as the
 * triggers that are fired, misfired and completed.
 * 
 * <p>
 * They will be called by the {@link Scheduler} whenever a trigger operation
 * happens.
 * </p>
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class JobTriggerListener extends TriggerListenerSupport {

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
	private static final Logger LOGGER = AppConfig.getLogger(JobTriggerListener.class);

	@Override
	public synchronized void triggerMisfired(Trigger trigger) {

		LOGGER.info(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger misfired started");

		/*
		 * ------------------Trigger MisFired ---------------
		 */
		try {
			// Insert SYNC Transaction
			syncTransactionManager.createSyncTransaction(RegistrationConstants.JOB_TRIGGER_MIS_FIRED,
					RegistrationConstants.JOB_TRIGGER_MIS_FIRED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					trigger.getKey().getName());
		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(baseUncheckedException));
		}

		LOGGER.info(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger misfired ended");

	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public synchronized void triggerFired(Trigger trigger, JobExecutionContext context) {

		LOGGER.info(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger fired started");

		/* TRIGGER Fired */
		try {
			// Insert SYNC Transaction
			syncTransactionManager.createSyncTransaction(RegistrationConstants.JOB_TRIGGER_STARTED,
					RegistrationConstants.JOB_TRIGGER_STARTED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					jobManager.getJobId(context));
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					regBaseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(regBaseUncheckedException));
		}
		LOGGER.info(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger fired ended");

	}

	@Override
	public synchronized void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {

		LOGGER.info(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger completed started");

		try {
			// Insert SYNC Transaction
			syncTransactionManager.createSyncTransaction(RegistrationConstants.JOB_TRIGGER_COMPLETED,
					RegistrationConstants.JOB_TRIGGER_COMPLETED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					jobManager.getJobId(context));

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					regBaseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(regBaseUncheckedException));
		}

		/*
		 * -------------------TRIGGER Completed-------------------------------
		 */
		LOGGER.info(LoggerConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger completed ended");

	}

}
