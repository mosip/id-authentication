package io.mosip.registration.service.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.JobConfigDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJob;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.JobConfigurationService;

/**
 * implementation class of {@link JobConfigurationService}
 * 
 * @author YASWANTH S
 *
 */
@Service
public class JobConfigurationServiceImpl implements JobConfigurationService {

	@Autowired
	JobConfigDAO jobConfigDAO;

	/**
	 * Sheduler factory bean which will take Job and Trigger details and run jobs
	 * implicitly
	 */
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(JobConfigurationServiceImpl.class);

	/**
	 * sync job map with key as jobID and value as SyncJob
	 * (Entity)
	 */
	public static Map<String, SyncJob> SYNC_JOB_MAP = new HashMap<>();
	
	
	private static Map<String, Object> JOBDATASMAP = new HashMap<>();

	private ApplicationContext applicationContext;

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.JobConfigurationService#initiateJobs()
	 */
	@PostConstruct
	public void initiateJobs() {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was started");

		List<SyncJob> jobList = jobConfigDAO.getActiveJobs();
		jobList.forEach(syncJob -> {
			SYNC_JOB_MAP.put(syncJob.getId(), syncJob);
			
		});

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was completed");

	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.JobConfigurationService#startJobs(org.springframework.context.ApplicationContext)
	 */
	@SuppressWarnings("unchecked")
	public ResponseDTO startJobs(ApplicationContext applicationContext) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();
		this.applicationContext = applicationContext;

		SYNC_JOB_MAP.forEach((jobId, syncJob) -> {
			try {

				JobDetailFactoryBean jobDetailBean = new JobDetailFactoryBean();
				CronTriggerFactoryBean cronTriggerBean = new CronTriggerFactoryBean();

				BaseJob baseJob = null;

				// Get Job instance through application context
				baseJob = (BaseJob) applicationContext.getBean(syncJob.getApiName());

				jobDetailBean.setJobClass(baseJob.jobClass());
				jobDetailBean.setName(syncJob.getId());
				
				JOBDATASMAP.put("applicationContext", applicationContext);

				// putting application context in job data map

				jobDetailBean.setJobDataAsMap(JOBDATASMAP);
				jobDetailBean.afterPropertiesSet();

				cronTriggerBean.setJobDetail(jobDetailBean.getObject());
				cronTriggerBean.setCronExpression(syncJob.getSyncFrequency());
				cronTriggerBean.setName(syncJob.getId());
				cronTriggerBean.afterPropertiesSet();
				
				schedulerFactoryBean.getScheduler().scheduleJob(jobDetailBean.getObject(), cronTriggerBean.getObject());
				setSuccessResponseDTO(responseDTO, RegistrationConstants.BATCH_JOB_START_SUCCESS_MESSAGE);

			} catch (SchedulerException | ParseException | NoSuchBeanDefinitionException exception) {
				setErrorResponseDTO(responseDTO, exception);
			}
		});
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation ended");

		return responseDTO;

	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.JobConfigurationService#stopJobs()
	 */
	public ResponseDTO stopJobs() {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();
		try {
			schedulerFactoryBean.getScheduler().shutdown();
			setSuccessResponseDTO(responseDTO, RegistrationConstants.BATCH_JOB_STOP_SUCCESS_MESSAGE);

		} catch (SchedulerException schedulerException) {
			setErrorResponseDTO(responseDTO, schedulerException);
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation ended");

		return responseDTO;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.JobConfigurationService#getCurrentRunningJobDetails()
	 */
	public ResponseDTO getCurrentRunningJobDetails() {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details started");

		ResponseDTO responseDTO=new ResponseDTO();
		try {

			// Get currently executing jobs from scheduler factory
			List<JobExecutionContext> executingJobList = schedulerFactoryBean.getScheduler()
					.getCurrentlyExecutingJobs();
			SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
			Map<String,Object> jobNames = new HashMap<>();
			for (JobExecutionContext jobExecutionContext : executingJobList) {
				
				//@see Need to be prepared as per businness requirement
				jobNames.put("jobName",jobExecutionContext.getJobDetail());
			}
			successResponseDTO.setOtherAttributes(jobNames);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			
		} catch (SchedulerException schedulerException) {
			setErrorResponseDTO(responseDTO, schedulerException);
			
		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details ended");

		return responseDTO;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.JobConfigurationService#executeJob(java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String apiName) {

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job started");
		ResponseDTO responseDTO = null;
		try {
			// Get Job using application context and api name
			BaseJob job = (BaseJob) applicationContext.getBean(apiName);

			// Job Invocation
			responseDTO = job.executeJob(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
			responseDTO = new ResponseDTO();
			setErrorResponseDTO(responseDTO, noSuchBeanDefinitionException);
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job ended");
		return responseDTO;
	}

	private ResponseDTO setErrorResponseDTO(ResponseDTO responseDTO, Exception exception) {
		LinkedList<ErrorResponseDTO> errorResponseDTOs = new LinkedList<>();

		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setCode(RegistrationConstants.BATCH_JOB_CODE);
		errorResponseDTO.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponseDTO.setMessage(exception.getMessage());
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);

		return responseDTO;
	}

	private ResponseDTO setSuccessResponseDTO(ResponseDTO responseDTO, String message) {

		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		successResponseDTO.setCode(RegistrationConstants.BATCH_JOB_CODE);
		successResponseDTO.setMessage(message);

		responseDTO.setSuccessResponseDTO(successResponseDTO);

		return responseDTO;
	}

}
