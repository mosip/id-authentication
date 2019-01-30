package io.mosip.registration.service.config.impl;

import java.sql.Timestamp;
import java.util.Calendar;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncTransactionDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncDataProcessDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.jobs.BaseJob;
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
	 * Scheduler factory bean which will take Job and Trigger details and run jobs
	 * implicitly
	 */
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private SyncTransactionDAO syncJobTransactionDAO;

	@Autowired
	private SyncJobControlDAO syncJobDAO;

	@Autowired
	private GlobalParamDAO globalParamDAO;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(JobConfigurationServiceImpl.class);

	/**
	 * Active sync job map with key as jobID and value as SyncJob (Entity)
	 */
	private Map<String, SyncJobDef> syncActiveJobMap = new HashMap<>();

	/**
	 * Sync job map with key as jobID and value as SyncJob (Entity)
	 */
	private Map<String, SyncJobDef> syncJobMap = new HashMap<>();

	private boolean isSchedulerRunning = false;

	@Value("${SYNC_TRANSACTION_NO_OF_DAYS_LIMIT}")
	private int syncTransactionHistoryLimitDays;

	private ApplicationContext applicationContext;

	private JobDataMap jobDataMap = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#initiateJobs()
	 */
	@PostConstruct
	public void initiateJobs() {
		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was started");

		/* Get All Jobs */
		List<SyncJobDef> jobDefs = jobConfigDAO.getAll();
		jobDefs.forEach(syncJob -> {

			/* All Jobs */
			syncJobMap.put(syncJob.getId(), syncJob);

			/* Active Jobs Map */
			if (syncJob.getIsActive()) {
				syncActiveJobMap.put(syncJob.getId(), syncJob);
			}
		});

		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was completed");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#startJobs(org.
	 * springframework.context.ApplicationContext)
	 */
	public ResponseDTO startScheduler(ApplicationContext applicationContext) {
		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();

		/* Check Whether Scheduler is running or not */
		if (isSchedulerRunning) {
			return setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STARTED, null);
		} else {
			schedulerFactoryBean.start();
			isSchedulerRunning = true;
			Map<String, Object> jobDataAsMap = new HashMap<>();

			this.applicationContext = applicationContext;
			jobDataAsMap.put("applicationContext", applicationContext);
			jobDataAsMap.putAll(syncJobMap);

			jobDataMap = new JobDataMap(jobDataAsMap);

			loadScheduler(responseDTO);

		}

		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation ended");

		return responseDTO;

	}

	private void loadScheduler(ResponseDTO responseDTO) {
		syncActiveJobMap.forEach((jobId, syncJob) -> {
			try {
				if (syncJob.getParentSyncJobId() == null && responseDTO.getErrorResponseDTOs() == null
						&& isSchedulerRunning && !schedulerFactoryBean.getScheduler().checkExists(new JobKey(jobId))) {

					// Get Job instance through application context
					BaseJob baseJob = (BaseJob) applicationContext.getBean(syncJob.getApiName());

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

					/* Clear Scheduler */
					clearScheduler();

				} catch (SchedulerException schedulerException) {
					LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE,
							RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
							schedulerException.getMessage());
				}

				/* Stop Scheduler Factory */
				schedulerFactoryBean.stop();

				isSchedulerRunning = false;

				/* Error Response */
				setErrorResponse(responseDTO, RegistrationConstants.START_SCHEDULER_ERROR_MESSAGE, null);

			}

			if (isSchedulerRunning) {
				setSuccessResponse(responseDTO, RegistrationConstants.BATCH_JOB_START_SUCCESS_MESSAGE, null);
			}

		}

		);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.JobConfigurationService#stopScheduler()
	 */
	public ResponseDTO stopScheduler() {
		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			if (schedulerFactoryBean.isRunning()) {

				clearScheduler();
				schedulerFactoryBean.stop();
				isSchedulerRunning = false;
				setSuccessResponse(responseDTO, RegistrationConstants.BATCH_JOB_STOP_SUCCESS_MESSAGE, null);

			} else {
				setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STOPPED, null);

			}
		} catch (SchedulerException | RuntimeException schedulerException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, schedulerException.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.STOP_SCHEDULER_ERROR_MESSAGE, null);

		}

		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation ended");

		return responseDTO;
	}

	private void clearScheduler() throws SchedulerException {
		/* Clear Scheduler */
		schedulerFactoryBean.getScheduler().clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#
	 * getCurrentRunningJobDetails()
	 */
	public ResponseDTO getCurrentRunningJobDetails() {
		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {

			// Get currently executing jobs from scheduler factory
			List<JobExecutionContext> executingJobList = schedulerFactoryBean.getScheduler()
					.getCurrentlyExecutingJobs();

			if (isNull(executingJobList) || isEmpty(executingJobList)) {
				setErrorResponse(responseDTO, RegistrationConstants.NO_JOBS_RUNNING, null);
			} else {
				List<SyncDataProcessDTO> dataProcessDTOs = executingJobList.stream().map(jobExecutionContext -> {

					SyncJobDef syncJobDef = syncJobMap.get(jobExecutionContext.getJobDetail().getKey().getName());

					return constructDTO(syncJobDef.getId(), syncJobDef.getName(), RegistrationConstants.JOB_RUNNING,
							new Timestamp(System.currentTimeMillis()).toString());

				}).collect(Collectors.toList());

				setResponseDTO(dataProcessDTOs, responseDTO, null, RegistrationConstants.NO_JOBS_RUNNING);

			}

		} catch (SchedulerException schedulerException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, schedulerException.getMessage());

			setErrorResponse(responseDTO, RegistrationConstants.CURRENT_JOB_DETAILS_ERROR_MESSAGE, null);

		}

		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job started");
		ResponseDTO responseDTO = null;
		try {

			SyncJobDef syncJobDef = syncActiveJobMap.get(jobId);

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
		LOGGER.info(RegistrationConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
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

		/* Fetch Sync control records */
		List<SyncControl> syncControls = syncJobDAO.findAll();

		if (!isNull(syncControls) && !isEmpty(syncControls)) {
			List<SyncDataProcessDTO> syncDataProcessDTOs = syncControls.stream().map(syncControl -> {

				String jobName = (syncJobMap.get(syncControl.getSyncJobId()) == null)
						? RegistrationConstants.JOB_UNKNOWN
						: syncJobMap.get(syncControl.getSyncJobId()).getName();

				String lastUpdTimes = (syncControl.getUpdDtimes() == null) ? syncControl.getCrDtime().toString()
						: syncControl.getUpdDtimes().toString();

				return constructDTO(syncControl.getSyncJobId(), jobName, RegistrationConstants.JOB_COMPLETED,
						lastUpdTimes);

			}).collect(Collectors.toList());

			setResponseDTO(syncDataProcessDTOs, responseDTO, null, RegistrationConstants.NO_JOB_COMPLETED);

		} else {
			setErrorResponse(responseDTO, RegistrationConstants.NO_JOB_COMPLETED, null);
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

		GlobalParam globalParam = globalParamDAO.get(RegistrationConstants.SYNC_TRANSACTION_NO_OF_DAYS_LIMIT);

		if (globalParam != null && globalParam.getVal() != null) {
			int syncTransactionConfiguredDays = Integer.parseInt(globalParam.getVal());

			/* Get Calendar instance */
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Timestamp(System.currentTimeMillis()));
			cal.add(Calendar.DATE, -syncTransactionConfiguredDays);

			/* To-Date */
			Timestamp req = new Timestamp(cal.getTimeInMillis());

			/* Get All sync Transaction Details from DataBase */
			List<SyncTransaction> syncTransactionList = syncJobTransactionDAO.getSyncTransactions(req,RegistrationConstants.JOB_TRIGGER_POINT_USER);

			if (!isNull(syncTransactionList) && !isEmpty(syncTransactionList)) {

				/* Reverse the list order, so that we can go through recent transactions */
				Collections.reverse(syncTransactionList);

				List<SyncDataProcessDTO> syncDataProcessDTOs = syncTransactionList.stream().map(syncTransaction -> {

					String jobName = (syncJobMap.get(syncTransaction.getSyncJobId()) == null)
							? RegistrationConstants.JOB_UNKNOWN
							: syncJobMap.get(syncTransaction.getSyncJobId()).getName();

					return constructDTO(syncTransaction.getSyncJobId(), jobName, syncTransaction.getStatusCode(),
							syncTransaction.getCrDtime().toString());

				}).collect(Collectors.toList());

				setResponseDTO(syncDataProcessDTOs, responseDTO, null, RegistrationConstants.NO_JOBS_TRANSACTION);

			} else {
				setErrorResponse(responseDTO, RegistrationConstants.NO_JOBS_TRANSACTION, null);
			}
		}

		return responseDTO;
	}

	private SyncDataProcessDTO constructDTO(String jobId, String jobName, String statusCode, String crDtimes) {
		/* create new Sync Data Process DTO */
		return new SyncDataProcessDTO(jobId, jobName, statusCode, crDtimes);

	}

	private void setResponseDTO(List<SyncDataProcessDTO> syncDataProcessDTOs, ResponseDTO responseDTO,
			String successMsg, String errorMsg) {

		/* Set Response DTO with Error or Success result */

		if (isNull(syncDataProcessDTOs) || isEmpty(syncDataProcessDTOs)) {
			setErrorResponse(responseDTO, errorMsg, null);

		} else {
			HashMap<String, Object> attributes = new HashMap<>();
			attributes.put(RegistrationConstants.SYNC_DATA_DTO, syncDataProcessDTOs);

			setSuccessResponse(responseDTO, successMsg, attributes);
		}
	}

}
