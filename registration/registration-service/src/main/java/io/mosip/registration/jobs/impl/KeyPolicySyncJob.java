package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.PolicySyncService;

/**
 * This is a job to sync the packet status
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Component(value = "keyPolicySyncJob")
public class KeyPolicySyncJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */
	@Autowired
	private PolicySyncService policySyncService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(KeyPolicySyncJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.KEY_POLICY_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			if (context != null) {
				this.jobId = loadContext(context);

			}

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.KEY_POLICY_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		// Run the Parent JOB always first
		SessionContext sessionContext = SessionContext.getInstance();
		String centerId = sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId();

		this.responseDTO = policySyncService.fetchPolicy(centerId);

		// To run the child jobs after the parent job Success
		if (responseDTO.getSuccessResponseDTO() != null && context != null) {
			executeChildJob(jobId, jobMap);
		}

		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.KEY_POLICY_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.debug(RegistrationConstants.KEY_POLICY_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");
		SessionContext sessionContext = SessionContext.getInstance();
		String centerId = sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId();
		
		this.responseDTO = policySyncService.fetchPolicy(centerId);
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.KEY_POLICY_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
