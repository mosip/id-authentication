package io.mosip.registration.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.manager.BaseTransactionManager;
import io.mosip.registration.service.impl.JobConfigurationServiceImpl;

/**
 * This class gives the information of job trigger
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class JobTriggerListener extends TriggerListenerSupport {

	/**
	 * Autowires the SncTransactionManagerImpl, which Have the functionalities to
	 * get the job and to create sync transaction
	 */
	@Autowired
	private BaseTransactionManager baseTransactionManager;

	/**
	 * LOGGER for logging
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(JobTriggerListener.class);

	@Override
	public void triggerMisfired(Trigger trigger) {

		System.out.println("Trigger REJECTED -1");
		
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger mis-fired started");

		/*
		 * ------------------Trigger MisFired ---------------
		 */
		try {
			//Insert SYNC Transaction
			baseTransactionManager.createSyncTransaction(RegistrationConstants.JOB_TRIGGER_FAILED,
					RegistrationConstants.JOB_TRIGGER_FAILED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					baseTransactionManager.getJob(trigger));
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger mis-fired ended");

		System.out.println("Trigger REJECTED -2");
		
		
	}

	@Override
	public String getName() {
		return "listener name";
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {

		System.out.println("Trigger Fired -1");
		
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger fired started");

		/* TRIGGER Fired */
		try {
			//Insert SYNC Transaction
			baseTransactionManager.createSyncTransaction(RegistrationConstants.JOB_TRIGGER_STARTED,
					RegistrationConstants.JOB_TRIGGER_STARTED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					baseTransactionManager.getJob(context));
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger fired ended");

		System.out.println("Trigger Fired -2");
		
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {

		System.out.println("Trigger Comp -1");
		
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger completed started");

		try {
			//Insert SYNC Transaction
			baseTransactionManager.createSyncTransaction(RegistrationConstants.JOB_TRIGGER_COMPLETED,
					RegistrationConstants.JOB_TRIGGER_COMPLETED, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
					baseTransactionManager.getJob(context));

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_PROCESS_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}

		/*
		 * -------------------TRIGGER Completed-------------------------------
		 */
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_TRIGGER_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "trigger completed ended");

		System.out.println("Trigger Comp -2");
		
	}

}
