package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.packet.RegistrationPacketVirusScanService;

/**
 * This is a job to scan the Registration packets for viruses.
 * 
 * <p>
 * This Job will be automatically triggered based on sync_frequency which has in
 * local DB.
 * </p>
 * 
 * <p>
 * If Sync_frequency = "0 0 11 * * ?" this job will be triggered everyday 11:00
 * AM, if it was missed on 11:00 AM, trigger on immediate application launch.
 * </p>
 * 
 * @author SARAVANAKUMAR G
 * @since 1.0.0
 *
 */
@Component(value = "registrationPacketVirusScanJob")
public class RegistrationPacketVirusScanJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */

	@Autowired
	private RegistrationPacketVirusScanService packetVirusScanService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPacketVirusScanJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(LoggerConstants.REG_PACKET_VIRUS_SCAN, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {

			this.jobId = loadContext(context);
			packetVirusScanService = applicationContext.getBean(RegistrationPacketVirusScanService.class);

			// Run the Parent JOB always first
			this.responseDTO = packetVirusScanService.scanPacket();

			// To run the child jobs after the parent job Success
			if (responseDTO.getSuccessResponseDTO() != null && context != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(LoggerConstants.REG_PACKET_VIRUS_SCAN, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		LOGGER.debug(LoggerConstants.REG_PACKET_VIRUS_SCAN, RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.debug(LoggerConstants.REG_PACKET_VIRUS_SCAN, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		this.responseDTO = packetVirusScanService.scanPacket();
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(LoggerConstants.REG_PACKET_VIRUS_SCAN, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
