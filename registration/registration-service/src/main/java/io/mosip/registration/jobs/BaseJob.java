package io.mosip.registration.jobs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;

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
	 * Autowires job manager for to get Job id functionality
	 */
	@Autowired
	protected JobManager jobManager;

	/**
	 * The SncTransactionManagerImpl, which Have the functionalities to get the job
	 * and to create sync transaction
	 */
	@Autowired
	protected SyncManager syncManager;

	protected  String jobId;

	protected  String triggerPoint;

	protected  ResponseDTO responseDTO;

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
	 * @return Response of execution
	 */
	public abstract ResponseDTO executeJob(String triggerPoint, String jobId);

	/**
	 * Job Execution process
	 * 
	 * @param currentJobID
	 *            current job executing
	 */
	public void executeChildJob(String currentJobID, Map<String, SyncJobDef> jobMap) {

		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execution started");

		try {

			// Check for current job's child
			jobMap.forEach((jobIdForChild, childJob) -> {
				if (childJob.getParentSyncJobId() != null && childJob.getParentSyncJobId().equals(currentJobID)) {

					// Parent SyncJob
					BaseJob parentBaseJob = (BaseJob) applicationContext.getBean(childJob.getApiName());

					// Response of parentBaseJob
					ResponseDTO childJobResponseDTO = parentBaseJob.executeJob(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
							childJob.getId());

					if (childJobResponseDTO.getSuccessResponseDTO() != null) {
						// Execute its next child Job
						executeChildJob(childJob.getId(), jobMap);
					}
				}
			});

		} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
			LOGGER.error(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					noSuchBeanDefinitionException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					noSuchBeanDefinitionException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execution Ended");

	}

	public ResponseDTO syncTransactionUpdate(ResponseDTO responseDTO, String triggerPoint, String syncJobId) {

		if (responseDTO != null) {
			try {
				if (responseDTO.getSuccessResponseDTO() != null) {

					// Insert Sync Transaction of executed with Success
					SyncTransaction syncTransaction = syncManager.createSyncTransaction(
							RegistrationConstants.JOB_EXECUTION_SUCCESS, RegistrationConstants.JOB_EXECUTION_SUCCESS,
							triggerPoint, syncJobId);

					// Insert Sync Control transaction
					syncManager.createSyncControlTransaction(syncTransaction);

					Map<String, Object> attributes = new HashMap<>();
					attributes.put("syncTransaction", syncTransaction);

					SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
					successResponseDTO.setOtherAttributes(attributes);

				} else if (responseDTO.getErrorResponseDTOs() != null) {

					// Insert Sync Transaction of executed with failure
					syncManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_FAILURE,
							RegistrationConstants.JOB_EXECUTION_FAILURE, triggerPoint, syncJobId);

				}
			} catch (RegBaseUncheckedException regBaseUncheckedException) {

				LOGGER.error(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
						RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						regBaseUncheckedException.getMessage());
				LinkedList<ErrorResponseDTO> errorResponseDTOs = new LinkedList<>();

				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setInfoType(RegistrationConstants.ERROR);
				errorResponseDTO.setMessage(regBaseUncheckedException.getMessage());

				errorResponseDTOs.add(errorResponseDTO);

				responseDTO.setErrorResponseDTOs(errorResponseDTOs);

			}

		}
		return responseDTO;

	}

	public String loadContext(JobExecutionContext context) {
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

			// Get Job Map
			if (jobMap == null) {
				jobMap = jobManager.getChildJobs(context);

			}

		} catch (NoSuchBeanDefinitionException | RegBaseUncheckedException exception) {

			LOGGER.error(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					exception.getMessage());
		} catch (NullPointerException nullPointerException) {

			LOGGER.error(RegistrationConstants.BASE_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, nullPointerException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());

		}

		// Get Current JobId
		return jobManager.getJobId(context);

	}
}
