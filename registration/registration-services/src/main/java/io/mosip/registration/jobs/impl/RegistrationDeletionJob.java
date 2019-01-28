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
import io.mosip.registration.service.packet.RegPacketStatusService;

/**
 * This is a job to delete the registration packets
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component(value = "registrationDeletionJob")
public class RegistrationDeletionJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */
	@Autowired
	private RegPacketStatusService packetStatusService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationDeletionJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_DELETION_JOB_LOGGER_TITLE,
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			this.jobId = loadContext(context);
			packetStatusService = applicationContext.getBean(RegPacketStatusService.class);

			// Run the Parent JOB always first
			this.responseDTO = packetStatusService.deleteReRegistrationPackets();

			// To run the child jobs after the parent job Success
			if (responseDTO.getSuccessResponseDTO() != null && context != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_DELETION_JOB_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		LOGGER.debug(RegistrationConstants.REGISTRATION_DELETION_JOB_LOGGER_TITLE,
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"job execute internal Ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.jobs.BaseJob#executeJob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_DELETION_JOB_LOGGER_TITLE,
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID, "execute Job started");

		this.responseDTO = packetStatusService.deleteReRegistrationPackets();

		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.REGISTRATION_DELETION_JOB_LOGGER_TITLE,
				RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
