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
import io.mosip.registration.service.UserMachineMappingService;
import io.mosip.registration.service.impl.UserMachineMappingServiceImpl;

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
        this.responseDTO=userMachineMappingService.syncUserDetails();
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
			
			UserMachineMappingService userMachineMappingService=applicationContext.getBean(UserMachineMappingServiceImpl.class);
			this.responseDTO=userMachineMappingService.syncUserDetails();
			
			if (responseDTO.getSuccessResponseDTO() != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

			
		}catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
		}
		
		
		LOGGER.debug(RegistrationConstants.REG_USER_MAPPING_SYNC_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal ended");
			

	}

}
