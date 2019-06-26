package io.mosip.registration.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * The class BaseJob was a quartzJobBean which gives the information of job and
 * its functionalities.This class will get all the active jobids and run that
 * particular jobs by calling that services.
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public abstract class BaseJob extends QuartzJobBean {

	/**
	 * Application context to get Bean
	 */
	protected ApplicationContext applicationContext = null;

	/**
	 * Autowires job manager to get Job id functionality
	 */
	@Autowired
	protected JobManager jobManager;

	/**
	 * The SncTransactionManagerImpl, which Have the functionalities to get the job
	 * and to create sync transaction
	 */
	@Autowired
	protected SyncManager syncManager;

	protected String jobId;

	protected String triggerPoint;

	protected ResponseDTO responseDTO;

	private static Map<String, String> completedJobMap = new HashMap<>();

	public static final List<String> successJob = new ArrayList<>();

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BaseJob.class);

	protected Map<String, SyncJobDef> jobMap;

	/**
	 * To get current job class
	 * 
	 * @return class
	 */
	@SuppressWarnings("rawtypes")
	public Class jobClass() {
		return this.getClass();
	}

	/**
	 * To execute the specified Job invocation
	 * 
	 * @param triggerPoint
	 *            the triggered person
	 * @param jobId
	 *            the job ID
	 * @return Response of execution
	 */
	public abstract ResponseDTO executeJob(String triggerPoint, String jobId);

	/**
	 * If there is any job called by the currently running job then this method will
	 * gets called to finish the child jobs first
	 * 
	 * @param currentJobID
	 *            current job executing
	 * @param jobMap
	 *            is a job's map
	 */
	public synchronized void executeChildJob(String currentJobID, Map<String, SyncJobDef> jobMap) {

		LOGGER.info(LoggerConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execution started");

		try {

			/* Check for current job's child */
			jobMap.forEach((jobIdForChild, childJob) -> {
				if (childJob.getParentSyncJobId() != null && childJob.getParentSyncJobId().equals(currentJobID)) {

					/* Parent SyncJob */
					BaseJob parentBaseJob = (BaseJob) applicationContext.getBean(childJob.getApiName());

					removeCompletedJobInMap(childJob.getId());

					/* Response of parentBaseJob */
					ResponseDTO childJobResponseDTO = parentBaseJob
							.executeJob(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM, childJob.getId());

					if (childJobResponseDTO.getSuccessResponseDTO() != null) {
						/* Execute its next child Job */
						executeChildJob(childJob.getId(), jobMap);
					}
				}
			});

		} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
			LOGGER.error(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					noSuchBeanDefinitionException.getMessage()
							+ ExceptionUtils.getStackTrace(noSuchBeanDefinitionException));

			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					noSuchBeanDefinitionException.getMessage());
		}

		LOGGER.info(LoggerConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execution Ended");

	}

	/**
	 * Once all the jobs are completed then this method will save this transaction
	 * in the table
	 * 
	 * @param responseDTO
	 *            - the {@link ResponseDTO}
	 * @param triggerPoint
	 *            - the trigger point which indicates whether manual or batch jobs
	 * @param syncJobId
	 *            - the sync job ID
	 */
	public synchronized void syncTransactionUpdate(ResponseDTO responseDTO, String triggerPoint, String syncJobId) {

		String status = (responseDTO != null && responseDTO.getSuccessResponseDTO() != null)
				? RegistrationConstants.JOB_EXECUTION_SUCCESS
				: RegistrationConstants.JOB_EXECUTION_FAILURE;
		try {

			addToCompletedJobMap(syncJobId, status);

			/* Insert Sync Transaction of executed with Success/failure */
			SyncTransaction syncTransaction = syncManager.createSyncTransaction(status, status, triggerPoint,
					syncJobId);

			if (RegistrationConstants.JOB_EXECUTION_SUCCESS.equals(status)) {
				/* Insert Sync Control transaction */
				syncManager.createSyncControlTransaction(syncTransaction);
			}

		} catch (RegBaseUncheckedException regBaseUncheckedException) {

			LOGGER.error(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					regBaseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(regBaseUncheckedException));

		}

	}

	protected synchronized String loadContext(JobExecutionContext context) {
		try {

			/*
			 * Get Application Context from JobExecutionContext's job detail and set
			 * application_Context
			 */
			setApplicationContext((ApplicationContext) context.getJobDetail().getJobDataMap()
					.get(RegistrationConstants.APPLICATION_CONTEXT));

			/* Sync Transaction Manager */
			syncManager = this.applicationContext.getBean(SyncManager.class);

			/* Job Manager */
			jobManager = this.applicationContext.getBean(JobManager.class);

			triggerPoint = RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;

			/* Get Job Map */
			if (jobMap == null) {
				jobMap = jobManager.getChildJobs(context);

			}

		} catch (NoSuchBeanDefinitionException | RegBaseUncheckedException exception) {

			LOGGER.error(LoggerConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		} catch (NullPointerException nullPointerException) {

			LOGGER.error(LoggerConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					nullPointerException.getMessage() + ExceptionUtils.getStackTrace(nullPointerException));

			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());

		}

		/* Get Current JobId */
		String currentJobId = jobManager.getJobId(context);

		removeCompletedJobInMap(currentJobId);

		return currentJobId;

	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		if (applicationContext != null) {
			this.applicationContext = applicationContext;
		}
	}

	public void addToCompletedJobMap(String jobId, String status) {
		completedJobMap.put(jobId, status);
		if (status.contains("success")) {
			successJob.add(jobId);
		}
	}

	public static Map<String, String> getCompletedJobMap() {
		return completedJobMap;
	}

	public static void clearCompletedJobMap() {
		completedJobMap.clear();
	}

	public static void removeCompletedJobInMap(String jobId) {
		completedJobMap.remove(jobId);
	}

}
