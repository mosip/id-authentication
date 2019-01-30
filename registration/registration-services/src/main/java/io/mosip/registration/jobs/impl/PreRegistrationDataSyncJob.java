package io.mosip.registration.jobs.impl;

import java.util.LinkedList;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
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
		LOGGER.info(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			this.jobId = loadContext(context);
			preRegistrationDataSyncService = applicationContext.getBean(PreRegistrationDataSyncService.class);

			// Run the Parent JOB always first
			this.responseDTO = preRegistrationDataSyncService.getPreRegistrationIds(jobId);

			// To run the child jobs after the parent job Success
			if (responseDTO.getSuccessResponseDTO() != null && context != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage());
			throw baseUncheckedException;
		} catch (RuntimeException runtimeException) {
			this.responseDTO = new ResponseDTO();
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			LinkedList<ErrorResponseDTO> errorResponsesList = new LinkedList<>();
			errorResponsesList.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponsesList);
		}

		LOGGER.info(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.info(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		this.responseDTO = preRegistrationDataSyncService.getPreRegistration(jobId);
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.info(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
