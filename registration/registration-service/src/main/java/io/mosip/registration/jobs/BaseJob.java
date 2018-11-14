package io.mosip.registration.jobs;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJob;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.manager.BaseTransactionManager;
import io.mosip.registration.manager.impl.SyncTransactionManagerImpl;
import io.mosip.registration.service.impl.JobConfigurationServiceImpl;

/**
 * The class BaseJob was a quartzJobBean which gives the information of job and
 * its functionalities
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public abstract class BaseJob extends QuartzJobBean {

	protected ApplicationContext applicationContext = null;

	/**
	 * LOGGER for logging
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(SyncTransaction.class);

	/**
	 * To get current job class
	 * 
	 * @return class
	 */
	@SuppressWarnings("rawtypes")
	public Class jobClass() {
		return this.getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	protected void executeInternal(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		BaseTransactionManager baseTransactionManager = null;

		try {

			/*
			 * Get Application Context from JobExecutionContext's job detail
			 */
			this.applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap()
					.get("applicationContext");

			baseTransactionManager = this.applicationContext.getBean(BaseTransactionManager.class);

			this.executeParentJob(baseTransactionManager.getJob(context).getId());

		} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					noSuchBeanDefinitionException.getMessage());
		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());

		}

		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	/**
	 * To execute the specified Job invocation
	 * 
	 * @param triggerPoint
	 *            the triggered person
	 * @return Response of execution
	 */
	public abstract ResponseDTO executeJob(String triggerPoint);

	/**
	 * Job Execution process
	 * 
	 * @param currentJobID
	 *            current job executing
	 */
	public void executeParentJob(String currentJobID) {
		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execution started");

		if (JobConfigurationServiceImpl.SYNC_JOB_MAP.get(currentJobID) != null) {

			// Get Current SyncJob
			SyncJob syncJob = JobConfigurationServiceImpl.SYNC_JOB_MAP.get(currentJobID);
			if (syncJob.getParentSyncJobId() != null) {
				executeParentJob(syncJob.getParentSyncJobId());
			}

			try {
				//Get job from application context, which has to be executed
				BaseJob baseJob = (BaseJob) this.applicationContext.getBean(syncJob.getApiName());
				baseJob.executeJob(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

			} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
				throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
						noSuchBeanDefinitionException.getMessage());
			}

		}
		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execution Ended");

	}

}
