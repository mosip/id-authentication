package io.mosip.registration.jobs.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.LinkedList;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Delete the Pre-Registration Packets Based on the appointment date
 * @author M1046564
 *
 */
@Component(value = "preRegistrationPacketDeletionJob")
public class PreRegistrationPacketDeletionJob extends BaseJob {

	private static final Logger LOGGER = AppConfig.getLogger(PreRegistrationPacketDeletionJob.class);

	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.jobs.BaseJob#executeJob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {
		
		LOGGER.debug("REGISTRATION - PRE_REG_PACKET_DELETION_STARTED_CHILD_JOB - PRE_REGISTRATION_PACKET_DELETION_JOB", APPLICATION_NAME,
				APPLICATION_ID, "Pre-Registration Packet Deletion job started");
		
		this.responseDTO = preRegistrationDataSyncService.fetchAndDeleteRecords();
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);
		
		LOGGER.debug("REGISTRATION - PRE_REG_PACKET_DELETION_CHILD_JOB_ENDED - PRE_REGISTRATION_PACKET_DELETION_JOB", APPLICATION_NAME,
				APPLICATION_ID, "Pre-Registration Packet Deletion job ended");
		
		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug("REGISTRATION - PRE_REG_PACKET_DELETION_STARTED - PRE_REGISTRATION_PACKET_DELETION_JOB", APPLICATION_NAME,
				APPLICATION_ID, "Pre-Registration Packet Deletion job started");
		this.responseDTO = new ResponseDTO();

		try {
			if (context != null) {
				this.jobId = loadContext(context);
			}

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error("REGISTRATION - PRE_REG_PACKET_DELETION_ERROR - PRE_REGISTRATION_PACKET_DELETION_JOB",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		this.triggerPoint = (context != null) ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : triggerPoint;

		if (preRegistrationDataSyncService == null) {
			preRegistrationDataSyncService = applicationContext.getBean(PreRegistrationDataSyncService.class);
		}
		
		
		try {
			// Run the Parent JOB always first
			this.responseDTO = preRegistrationDataSyncService.fetchAndDeleteRecords();
			
		}
		catch(Exception exception) {
			LOGGER.error("PRE_REGISTRATION_PACKET_DELETION_JOB", RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.debug("REGISTRATION - PRE_REG_PACKET_DELETION_ENDED - PRE_REGISTRATION_PACKET_DELETION_JOB", APPLICATION_NAME,
				APPLICATION_ID, "Pre-Registration Packet Deletion job ended");

	}

}
