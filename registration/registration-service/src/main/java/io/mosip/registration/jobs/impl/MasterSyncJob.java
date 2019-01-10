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
import io.mosip.registration.service.MasterSyncService;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Component(value = "masterSyncJob")
public class MasterSyncJob extends BaseJob {

	/**
	 * The masterSyncService
	 */
	@Autowired
	private MasterSyncService masterSyncService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncJob.class);

	/**
	 * Execute internal.
	 *
	 * @param context
	 *            the context
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			if (context != null) {
				this.jobId = loadContext(context);
				masterSyncService = applicationContext.getBean(MasterSyncService.class);

			}
			// Run the Parent JOB always first
			this.responseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001);

			// To run the child jobs after the parent job Success
			if (responseDTO.getSuccessResponseDTO() != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");
	}

	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		this.responseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001);
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;
	}

}
