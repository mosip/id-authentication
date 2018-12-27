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
import io.mosip.registration.service.packet.RegPacketStatusService;

/**
 * This is a job to sync the packet status
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component(value = "packetSyncStatusJob")
public class PacketSyncStatusJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */
	@Autowired
	private RegPacketStatusService packetStatusService;
	
	
	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(PacketSyncStatusJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			if(context!=null) {
				this.jobId = loadContext(context);
				packetStatusService = applicationContext.getBean(RegPacketStatusService.class);

			}

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		
		this.triggerPoint  = (context!=null) ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : triggerPoint;
		
		try {
			// Run the Parent JOB always first
			this.responseDTO = packetStatusService.packetSyncStatus();

		} catch(Exception exception) {
			LOGGER.error(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
			LinkedList<ErrorResponseDTO> list=new  LinkedList<>();
			list.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(list);

		}
		// To run the child jobs after the parent job Success
		if (responseDTO.getSuccessResponseDTO() != null && context!=null) {
			executeChildJob(jobId, jobMap);
		}
		
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.jobs.BaseJob#executeJob(java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");
		
		this.responseDTO = packetStatusService.packetSyncStatus();
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);
		
		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		 return responseDTO;

	}

}
