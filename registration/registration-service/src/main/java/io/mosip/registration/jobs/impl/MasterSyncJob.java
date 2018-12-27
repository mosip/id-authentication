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
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.service.PolicySyncService;

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

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		this.triggerPoint = (context != null) ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : triggerPoint;

		
		try {
			// Run the Parent JOB always first
			this.responseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001);
			
		} catch(Exception exception) {
			LOGGER.error(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
			LinkedList<ErrorResponseDTO> list=new  LinkedList<>();
			list.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(list);

		}

		// To run the child jobs after the parent job Success
		if (responseDTO.getSuccessResponseDTO() != null && context != null) {
			executeChildJob(jobId, jobMap);
		}

		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

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
