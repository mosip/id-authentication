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
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.JobManager;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.MasterSyncService;

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

			// Sync Transaction Manager
			syncManager = this.applicationContext.getBean(SyncManager.class);

			// Job Manager
			jobManager = this.applicationContext.getBean(JobManager.class);

			// Get Current JobId
			String syncJobId = jobManager.getJobId(context);

			// Get Job Map
			Map<String, SyncJobDef> jobMap = jobManager.getChildJobs(context);

			ResponseDTO responseDTO = executeJob(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM, syncJobId);

			if (responseDTO.getSuccessResponseDTO() != null) {
				executeChildJob(syncJobId, jobMap);
			}

		} catch (NoSuchBeanDefinitionException | RegBaseUncheckedException exception) {

			LOGGER.error(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					exception.getMessage());
		} catch (NullPointerException nullPointerException) {

			LOGGER.error(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, nullPointerException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());

		}

		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	@Override
	public ResponseDTO executeJob(String jobId) {
		
		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		String triggerPoint = SessionContext.getInstance().getUserContext().getUserId();
		
		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return executeJob(triggerPoint, jobId);
	}

	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		ResponseDTO responseDTO = null;
		
		try {
			
			responseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001);
		
		} catch (RegBaseCheckedException exception) {

			LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
		}

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return syncTransactionUpdate(responseDTO, triggerPoint, jobId);

	}

}
