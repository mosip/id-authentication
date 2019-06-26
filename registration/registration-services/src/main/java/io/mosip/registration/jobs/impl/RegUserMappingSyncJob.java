package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.operator.UserMachineMappingService;
import io.mosip.registration.service.operator.impl.UserMachineMappingServiceImpl;

/**
 * This is a job to sync the user mapping.
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
 * @author Brahmananda Reddy
 *
 * @since 1.0.0
 */
@Component("regUserMappingSyncJob")
public class RegUserMappingSyncJob extends BaseJob {
	@Autowired
	private UserMachineMappingService userMachineMappingService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SynchConfigDataJob.class);

	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {
		LOGGER.debug(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");
		this.responseDTO = userMachineMappingService.syncUserDetails();
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;
	}

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {

		LOGGER.debug(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			this.jobId = loadContext(context);

			UserMachineMappingService userMachineMappingService = applicationContext
					.getBean(UserMachineMappingServiceImpl.class);
			this.responseDTO = userMachineMappingService.syncUserDetails();

			if (responseDTO.getSuccessResponseDTO() != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal ended");

	}

}
