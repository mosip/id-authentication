package io.mosip.registration.jobs.impl;

import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.manager.JobManager;
import io.mosip.registration.manager.SyncManager;
import io.mosip.registration.manager.impl.SyncManagerImpl;

@Component
public class JobDummy extends BaseJob{
	
	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SyncManagerImpl.class);


	@Override
	public ResponseDTO executeJob(String triggerPoint) {
		System.out.println("Executed JobDummy");
		ResponseDTO responseDTO=new ResponseDTO();
		responseDTO.setSuccessResponseDTO(new SuccessResponseDTO());
		return responseDTO;
	}

	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {
		System.out.println("Executed JobDummy");
		
		ResponseDTO responseDTO=new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");

		try {

			/*
			 * Get Application Context from JobExecutionContext's job detail
			 */
			this.applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap()
					.get("applicationContext");

			//Sync Transaction Manager
			syncManager = this.applicationContext.getBean(SyncManager.class);

			//Job Manager
			jobManager =this.applicationContext.getBean(JobManager.class);
			
			
			// Get Current JobId
			String syncJobId = jobManager.getJobId(context);

			// Get Job Map
			Map<String, SyncJobDef> jobMap = jobManager.getChildJobs(context);
			
			ResponseDTO responseDTO = executeJob(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,syncJobId);

			if(responseDTO.getSuccessResponseDTO()!=null) {
				executeChildJob(syncJobId, jobMap);
			}

		} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
			
			LOGGER.error(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, noSuchBeanDefinitionException.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					noSuchBeanDefinitionException.getMessage());
		} catch (NullPointerException nullPointerException) {
			
			LOGGER.error(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, nullPointerException.getMessage());
			
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());

		}

		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}
}
