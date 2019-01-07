package io.mosip.registration.service.config.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobTransactionDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncDataProcessDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.JobManager;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.JobConfigurationService;

/**
 * implementation class of {@link JobConfigurationService}
 * 
 * @author YASWANTH S
 *
 */
@Service
public class JobConfigurationServiceImpl extends BaseService implements JobConfigurationService {

	@Autowired
	private SyncJobConfigDAO jobConfigDAO;

	/**
	 * Sheduler factory bean which will take Job and Trigger details and run jobs
	 * implicitly
	 */
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	SyncJobTransactionDAO syncJobTransactionDAO;

	@Autowired
	JobManager jobManager;

	@Autowired
	SyncJobDAO syncJobDAO;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(JobConfigurationServiceImpl.class);

	/**
	 * sync job map with key as jobID and value as SyncJob (Entity)
	 */
	private Map<String, SyncJobDef> SYNC_JOB_MAP = new HashMap<>();

	private List<SyncJobDef> jobList;

	private boolean isSchedulerRunning = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#initiateJobs()
	 */
	@PostConstruct
	public void initiateJobs() {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was started");

		jobList = jobConfigDAO.getActiveJobs();
		jobList.forEach(syncJob -> SYNC_JOB_MAP.put(syncJob.getId(), syncJob));

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was completed");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#startJobs(org.
	 * springframework.context.ApplicationContext)
	 */
	@SuppressWarnings("unchecked")
	public ResponseDTO startScheduler(ApplicationContext applicationContext) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();

		/** Check Whether Scheduler is running or not */
		if (isSchedulerRunning) {
			return setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STARTED, null);
		} else {
			schedulerFactoryBean.start();
			isSchedulerRunning = true;
			Map<String, Object> jobDataAsMap = new HashMap<>();
			jobDataAsMap.put("applicationContext", applicationContext);
			jobDataAsMap.putAll(SYNC_JOB_MAP);

			JobDataMap jobDataMap = new JobDataMap(jobDataAsMap);

			SYNC_JOB_MAP.forEach((jobId, syncJob) -> {
				try {
					if (syncJob.getParentSyncJobId() == null && responseDTO.getErrorResponseDTOs() == null
							&& isSchedulerRunning
							&& !schedulerFactoryBean.getScheduler().checkExists(new JobKey(jobId))) {

						BaseJob baseJob = null;

						// Get Job instance through application context
						baseJob = (BaseJob) applicationContext.getBean(syncJob.getApiName());

						JobDetail jobDetail = JobBuilder.newJob(baseJob.jobClass()).withIdentity(syncJob.getId())
								.usingJobData(jobDataMap).build();

						CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().forJob(jobDetail)
								.withIdentity(syncJob.getId())
								.withSchedule(CronScheduleBuilder.cronSchedule(syncJob.getSyncFrequency())).build();

						schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);

					}
				} catch (SchedulerException | NoSuchBeanDefinitionException exception) {
					LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE,
							RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
							exception.getMessage());

					try {

						/** Clear Scheduler */
						schedulerFactoryBean.getScheduler().clear();

					} catch (SchedulerException schedulerException) {
						LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE,
								RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
								schedulerException.getMessage());
					}

					/** Stop Scheduler Factory */
					schedulerFactoryBean.stop();

					isSchedulerRunning = false;

					/** Error Response */
					setErrorResponse(responseDTO, RegistrationConstants.START_SCHEDULER_ERROR_MESSAGE, null);

				}

				if (isSchedulerRunning) {
					setSuccessResponse(responseDTO, RegistrationConstants.BATCH_JOB_START_SUCCESS_MESSAGE, null);
				}

			}

			);

		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation ended");

		return responseDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.JobConfigurationService#stopScheduler()
	 */
	public ResponseDTO stopScheduler() {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			if (schedulerFactoryBean.isRunning()) {

				schedulerFactoryBean.stop();
				isSchedulerRunning = false;
				setSuccessResponse(responseDTO, RegistrationConstants.BATCH_JOB_STOP_SUCCESS_MESSAGE, null);

			} else {
				setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STOPPED, null);

			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

			setErrorResponse(responseDTO, RegistrationConstants.STOP_SCHEDULER_ERROR_MESSAGE, null);

		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation ended");

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#
	 * getCurrentRunningJobDetails()
	 */
	public ResponseDTO getCurrentRunningJobDetails() {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {

			// Get currently executing jobs from scheduler factory
			List<JobExecutionContext> executingJobList = schedulerFactoryBean.getScheduler()
					.getCurrentlyExecutingJobs();

			List<SyncDataProcessDTO> dataProcessDTOs = null;
			Map<String, Object> attributes = new HashMap<>();

			if (executingJobList.isEmpty()) {
				setErrorResponse(responseDTO, RegistrationConstants.NO_JOBS_RUNNING, null);
			} else {
				dataProcessDTOs = executingJobList.stream().map(jobExecutionContext -> {

					/** Parse Job Information to SyncDataProcessDTO */
					JobDetail jobDetail = jobExecutionContext.getJobDetail();
					String jobId = jobDetail.getKey().getName();

					SyncJobDef syncJobDef = SYNC_JOB_MAP.get(jobId);

					return new SyncDataProcessDTO(syncJobDef.getId(), syncJobDef.getName(),
							RegistrationConstants.JOB_RUNNING, new Timestamp(System.currentTimeMillis()).toString());

				}).collect(Collectors.toList());

				attributes.put(RegistrationConstants.SYNC_DATA_DTO, dataProcessDTOs);

				setSuccessResponse(responseDTO, null, attributes);
			}

		} catch (SchedulerException schedulerException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, schedulerException.getMessage());

			setErrorResponse(responseDTO, RegistrationConstants.CURRENT_JOB_DETAILS_ERROR_MESSAGE, null);

		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details ended");

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.JobConfigurationService#executeJob(java.lang.
	 * String)
	 */
	@Override
	public ResponseDTO executeJob(ApplicationContext applicationContext, String jobId) {

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job started");
		ResponseDTO responseDTO = null;
		try {

			SyncJobDef syncJobDef = SYNC_JOB_MAP.get(jobId);

			// Get Job using application context and api name
			BaseJob job = (BaseJob) applicationContext.getBean(syncJobDef.getApiName());

			String triggerPoint = SessionContext.getInstance().getUserContext().getUserId();

			// Job Invocation
			responseDTO = job.executeJob(triggerPoint, jobId);

		} catch (NoSuchBeanDefinitionException | NullPointerException | IllegalArgumentException exception) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());

			responseDTO = new ResponseDTO();
			setErrorResponse(responseDTO, RegistrationConstants.EXECUTE_JOB_ERROR_MESSAGE, null);
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job ended");
		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.config.JobConfigurationService#
	 * getLastCompletedSyncJobs()
	 */
	@Override
	public ResponseDTO getLastCompletedSyncJobs() {

		ResponseDTO responseDTO = new ResponseDTO();

		/** Fetch Sync control records */
		List<SyncControl> syncControls = syncJobDAO.findAll();

		List<SyncDataProcessDTO> syncDataProcessDTOs = null;

		syncDataProcessDTOs = syncControls.stream().map(syncControl -> {

			String jobName = (SYNC_JOB_MAP.get(syncControl.getSyncJobId()) == null) ? syncControl.getId()
					: SYNC_JOB_MAP.get(syncControl.getSyncJobId()).getName();

			String lastUpdTimes = (syncControl.getUpdDtimes() == null) ? syncControl.getCrDtime().toString()
					: syncControl.getUpdDtimes().toString();

			return new SyncDataProcessDTO(syncControl.getId(), jobName, RegistrationConstants.JOB_COMPLETED,
					lastUpdTimes);

		}).collect(Collectors.toList());

		if (syncDataProcessDTOs.isEmpty()) {
			return setErrorResponse(responseDTO, RegistrationConstants.NO_JOB_COMPLETED, null);
		} else {
			HashMap<String, Object> attributes = new HashMap<>();
			attributes.put(RegistrationConstants.SYNC_DATA_DTO, syncDataProcessDTOs);

			setSuccessResponse(responseDTO, null, attributes);
		}

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.config.JobConfigurationService#
	 * getSyncJobsTransaction()
	 */
	@Override
	public ResponseDTO getSyncJobsTransaction() {

		ResponseDTO responseDTO = new ResponseDTO();
		List<SyncTransaction> syncTransactionList = null;

		/** Get All sync Transcation Details from DataBase */
		syncTransactionList = syncJobTransactionDAO.getAll();

		/** Reverese the list order, so that we can go through recent transactions */
		Collections.reverse(syncTransactionList);

		List<SyncDataProcessDTO> syncDataProcessDTOs = null;

		syncDataProcessDTOs = syncTransactionList.stream().map(syncTransaction -> {

			String jobName = (SYNC_JOB_MAP.get(syncTransaction.getSyncJobId()) == null) ? ""
					: SYNC_JOB_MAP.get(syncTransaction.getSyncJobId()).getName();

			return new SyncDataProcessDTO(syncTransaction.getSyncJobId(), jobName, syncTransaction.getStatusCode(),
					syncTransaction.getCrDtime().toString());

		}).collect(Collectors.toList());

		if (syncDataProcessDTOs.isEmpty()) {
			return setErrorResponse(responseDTO, RegistrationConstants.NO_JOBS_TRANSACTION, null);

		} else {
			HashMap<String, Object> attributes = new HashMap<>();
			attributes.put(RegistrationConstants.SYNC_DATA_DTO, syncDataProcessDTOs);

			setSuccessResponse(responseDTO, null, attributes);
		}

		return responseDTO;
	}

}
