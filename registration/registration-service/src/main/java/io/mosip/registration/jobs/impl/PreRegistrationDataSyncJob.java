package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;

/**
 * This is a job to sync the pre registrations
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component(value = "preRegistrationDataSyncJob")
public class PreRegistrationDataSyncJob extends BaseJob {

	private static final Logger LOGGER = AppConfig.getLogger(PreRegistrationDataSyncJob.class);

	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		String syncJobId = null;
		try {
			if(context!=null) {
				this.jobId = loadContext(context);
			}

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		this.triggerPoint = (context != null) ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : triggerPoint;

		// Run the Parent JOB always first
		this.responseDTO = preRegistrationDataSyncService.getPreRegistration(jobId);

		// To run the child jobs after the parent job Success
		if (responseDTO.getSuccessResponseDTO() != null && context != null) {
			executeChildJob(syncJobId, jobMap);
		}

		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.jobs.BaseJob#executeJob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		this.triggerPoint = triggerPoint;
		this.jobId = jobId;

		executeInternal(null);

		LOGGER.debug(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
