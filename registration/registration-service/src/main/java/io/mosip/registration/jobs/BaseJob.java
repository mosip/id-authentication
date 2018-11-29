package io.mosip.registration.jobs;

import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.manager.JobManager;
import io.mosip.registration.manager.SyncManager;

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

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BaseJob.class);

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

	public abstract ResponseDTO executeJob(String jobId);

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
			jobMap.forEach((jobId, childJob) -> {
				if (childJob.getParentSyncJobId() != null && childJob.getParentSyncJobId().equals(currentJobID)) {

					// Parent SyncJob
					BaseJob parentBaseJob = (BaseJob) applicationContext.getBean(childJob.getApiName());

					// Response of parentBaseJob
					ResponseDTO responseDTO = parentBaseJob.executeJob(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM,
							childJob.getId());

					if (responseDTO.getSuccessResponseDTO() != null) {
						// Execute its next child Job
						executeChildJob(childJob.getId(), jobMap);
					}
				}
			});

		} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
			LOGGER.error(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, noSuchBeanDefinitionException.getMessage());
			
			throw new RegBaseUncheckedException(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION,
					noSuchBeanDefinitionException.getMessage());
		}

	LOGGER.debug(RegistrationConstants.BASE_JOB_TITLE,RegistrationConstants.APPLICATION_NAME,RegistrationConstants.APPLICATION_ID,"job execution Ended");

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

				} else if (responseDTO.getErrorResponseDTOs() != null) {

					// Insert Sync Transaction of executed with failure
					syncManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_FAILURE,
							RegistrationConstants.JOB_EXECUTION_FAILURE, triggerPoint, syncJobId);

				}
			} catch (RegBaseUncheckedException regBaseUncheckedException) {
				
				LOGGER.error(RegistrationConstants.BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, regBaseUncheckedException.getMessage());
				
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
}
