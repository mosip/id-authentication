package io.mosip.registration.jobs.impl;

import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.JobManager;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;


/**
 * This is a job to sync the pre registrations
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class PreRegistrationDataSyncJob extends BaseJob{
	
	private static final Logger LOGGER = AppConfig.getLogger(PreRegistrationDataSyncJob.class);

	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;
	
	
	

	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {
		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistrationIds(jobId);

		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return syncTransactionUpdate(responseDTO, triggerPoint, jobId);
	}

	@Override
	public ResponseDTO executeJob(String jobId) {
		String triggerPoint = SessionContext.getInstance().getUserContext().getUserId();
		return executeJob(triggerPoint, jobId);
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

		} catch (NoSuchBeanDefinitionException | RegBaseUncheckedException exception) {
			
			LOGGER.error(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					exception.getMessage());
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
