package io.mosip.registration.service.config.impl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncTransactionDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncDataProcessDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.JobProcessListener;
import io.mosip.registration.jobs.JobTriggerListener;
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

	/**
	 * To Fetch Job Configuration details
	 */
	@Autowired
	private SyncJobConfigDAO jobConfigDAO;

	/**
	 * Scheduler factory bean which will take Job and Trigger details and run jobs
	 * implicitly
	 */
	private SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * To get Sync Transactions
	 */
	@Autowired
	private SyncTransactionDAO syncJobTransactionDAO;

	/**
	 * To get last completed transactions
	 */
	@Autowired
	private SyncJobControlDAO syncJobDAO;

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

	/**
	 * To send it in job detail as Base job needs application context
	 */
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * To load in JobDetail
	 */
	private JobDataMap jobDataMap = null;

	/**
	 * Base Job
	 */
	private BaseJob baseJob;

	private static List<String> restartableJobList = new LinkedList<>();

	/**
	 * create a parser based on provided definition
	 */
	private static CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));

	@Autowired
	private JobTriggerListener commonTriggerListener;
	@Autowired
	private JobProcessListener jobProcessListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#initiateJobs()
	 */
	@PostConstruct
	public void initiateJobs() {
		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was started");

		try {

			/* Registration Client Config Sync */
			restartableJobList.add("SCD_J00011");

			/* Get All Jobs */
			List<SyncJobDef> jobDefs = getJobs();

			if (!isNull(jobDefs) && !isEmpty(jobDefs)) {

				/* Set Job-map and active sync-job-map */
				setSyncJobMap(jobDefs);

				/* Get Scheduler frequency from global param */
				String syncDataFreq = getGlobalConfigValueOf(RegistrationConstants.SYNC_DATA_FREQ);

				if (syncDataFreq != null) {
					List<SyncJobDef> jobsToBeUpdated = new LinkedList<>();

					/* Store the jobs to be updated */
					for (SyncJobDef syncJobDef : jobDefs) {
						if (!syncDataFreq.equals(syncJobDef.getSyncFrequency())) {
							syncJobDef.setSyncFrequency(syncDataFreq);

							jobsToBeUpdated.add(syncJobDef);
						}

					}
					if (!isNull(jobsToBeUpdated) && !isEmpty(jobsToBeUpdated)) {
						/* Update Jobs */
						updateJobs(jobsToBeUpdated);
					}
				}

			}

			if (!syncActiveJobMap.isEmpty()) {

				/* Check and Execute missed triggers */
				executeMissedTriggers(syncActiveJobMap);

				schedulerFactoryBean = getSchedulerFactoryBean(String.valueOf(syncActiveJobMap.size()));

			}

		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

		} catch (Exception exception) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));

		}

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Jobs initiation was completed");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#startJobs(org.
	 * springframework.context.ApplicationContext)
	 */
	public ResponseDTO startScheduler() {
		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();

		/* Check Whether Scheduler is running or not */
		if (isSchedulerRunning()) {
			return setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STARTED, null);
		} else {
			try {
				schedulerFactoryBean.start();
				isSchedulerRunning = true;

				/* Job Data Map */
				Map<String, Object> jobDataAsMap = new WeakHashMap<>();
				jobDataAsMap.put("applicationContext", applicationContext);
				jobDataAsMap.putAll(syncJobMap);

				jobDataMap = new JobDataMap(jobDataAsMap);

				loadScheduler(responseDTO);
			} catch (RuntimeException runtimeException) {
				LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
				setErrorResponse(responseDTO, RegistrationConstants.START_SCHEDULER_ERROR_MESSAGE, null);
			}

		}

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation ended");

		return responseDTO;

	}

	private void loadScheduler(ResponseDTO responseDTO) {
		syncActiveJobMap.forEach((jobId, syncJob) -> {
			try {
				if (syncJob.getParentSyncJobId() == null && syncJob.getApiName() != null
						&& responseDTO.getErrorResponseDTOs() == null && isSchedulerRunning()
						&& !schedulerFactoryBean.getScheduler().checkExists(new JobKey(jobId))) {

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
				LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));

				/* Stop, Clear Scheduler and set Error response */
				setStartExceptionError(responseDTO);

			} catch (RuntimeException runtimeException) {
				LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
				setStartExceptionError(responseDTO);

			}

			if (isSchedulerRunning()) {
				setSuccessResponse(responseDTO, RegistrationConstants.BATCH_JOB_START_SUCCESS_MESSAGE, null);
			}

		}

		);
	}

	private void setStartExceptionError(ResponseDTO responseDTO) {

		try {
			/* Clear Scheduler */
			clearScheduler();

		} catch (SchedulerException schedulerException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					schedulerException.getMessage() + ExceptionUtils.getStackTrace(schedulerException));
		}

		/* Error Response */
		setErrorResponse(responseDTO, RegistrationConstants.START_SCHEDULER_ERROR_MESSAGE, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.JobConfigurationService#stopScheduler()
	 */
	public ResponseDTO stopScheduler() {
		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			if (schedulerFactoryBean.isRunning()) {

				/* Clear and Stop Scheduler */
				clearScheduler();

				setSuccessResponse(responseDTO, RegistrationConstants.BATCH_JOB_STOP_SUCCESS_MESSAGE, null);

			} else {
				setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STOPPED, null);

			}
		} catch (RuntimeException | SchedulerException schedulerException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					schedulerException.getMessage() + ExceptionUtils.getStackTrace(schedulerException));
			setErrorResponse(responseDTO, RegistrationConstants.STOP_SCHEDULER_ERROR_MESSAGE, null);

		}

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "stop jobs invocation ended");

		return responseDTO;
	}

	private void clearScheduler() throws SchedulerException {

		/* Clear Scheduler */
		schedulerFactoryBean.getScheduler().clear();
		schedulerFactoryBean.stop();
		isSchedulerRunning = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.JobConfigurationService#
	 * getCurrentRunningJobDetails()
	 */
	public ResponseDTO getCurrentRunningJobDetails() {
		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {

			if (schedulerFactoryBean != null && isSchedulerRunning()) {
				// Get currently executing jobs from scheduler factory
				List<JobExecutionContext> executingJobList = schedulerFactoryBean.getScheduler()
						.getCurrentlyExecutingJobs();

				if (isNull(executingJobList) || isEmpty(executingJobList)) {
					setErrorResponse(responseDTO, RegistrationConstants.NO_JOBS_RUNNING, null);
				} else {
					List<SyncDataProcessDTO> dataProcessDTOs = executingJobList.stream().map(jobExecutionContext -> {

						SyncJobDef syncJobDef = syncJobMap.get(jobExecutionContext.getJobDetail().getKey().getName());

						return constructDTO(syncJobDef.getId(), syncJobDef.getName(), RegistrationConstants.JOB_RUNNING,
								Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).toString());

					}).collect(Collectors.toList());

					setResponseDTO(dataProcessDTOs, responseDTO, null, RegistrationConstants.NO_JOBS_RUNNING);

				}
			} else {
				setErrorResponse(responseDTO, RegistrationConstants.NO_JOBS_RUNNING, null);
			}

		} catch (SchedulerException schedulerException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					schedulerException.getMessage() + ExceptionUtils.getStackTrace(schedulerException));

			setErrorResponse(responseDTO, RegistrationConstants.CURRENT_JOB_DETAILS_ERROR_MESSAGE, null);

		}

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get current running job details ended");

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.JobConfigurationService#executeJob(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String jobId, String triggerPoint) {

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job started");
		ResponseDTO responseDTO = null;
		try {

			SyncJobDef syncJobDef = syncActiveJobMap.get(jobId);

			if (syncJobDef != null && syncJobDef.getApiName() != null) {
				// Get Job using application context and api name
				baseJob = (BaseJob) applicationContext.getBean(syncJobDef.getApiName());

				BaseJob.removeCompletedJobInMap(jobId);

				// Job Invocation
				responseDTO = baseJob.executeJob(triggerPoint, jobId);

				if (responseDTO.getSuccessResponseDTO() != null) {
					baseJob.setApplicationContext(applicationContext);

					/* Execute all its child jobs */
					baseJob.executeChildJob(jobId, syncJobMap);
				}
			} else {
				responseDTO = new ResponseDTO();
				setErrorResponse(responseDTO, RegistrationConstants.EXECUTE_JOB_ERROR_MESSAGE, null);
			}

		} catch (NoSuchBeanDefinitionException | NullPointerException | IllegalArgumentException exception) {
			exception.printStackTrace(); //TODO added by Gaurav
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));

			responseDTO = new ResponseDTO();
			setErrorResponse(responseDTO, RegistrationConstants.EXECUTE_JOB_ERROR_MESSAGE, null);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

			responseDTO = new ResponseDTO();
			setErrorResponse(responseDTO, RegistrationConstants.EXECUTE_JOB_ERROR_MESSAGE, null);
		}
		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
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

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get Last Completed Jobs Started");

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

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get Last Completed Jobs Ended");

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

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get Sync Transaction Started");

		ResponseDTO responseDTO = new ResponseDTO();

		String val = getGlobalConfigValueOf(RegistrationConstants.SYNC_TRANSACTION_NO_OF_DAYS_LIMIT);

		if (val != null) {
			int syncTransactionConfiguredDays = Integer.parseInt(val);

			/* Get Calendar instance */
			Calendar cal = Calendar.getInstance();
			cal.setTime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
			cal.add(Calendar.DATE, -syncTransactionConfiguredDays);

			/* To-Date */
			Timestamp req = new Timestamp(cal.getTimeInMillis());

			/* Get All sync Transaction Details from DataBase */
			List<SyncTransaction> syncTransactionList = syncJobTransactionDAO.getSyncTransactions(req,
					RegistrationConstants.JOB_TRIGGER_POINT_USER);

			if (!isNull(syncTransactionList) && !isEmpty(syncTransactionList)) {

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

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "get Sync Transaction Ended");

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
			Map<String, Object> attributes = new WeakHashMap<>();
			attributes.put(RegistrationConstants.SYNC_DATA_DTO, syncDataProcessDTOs);

			setSuccessResponse(responseDTO, successMsg, attributes);
		}
	}

	private List<SyncJobDef> getJobs() {
		return jobConfigDAO.getAll();
	}

	private void setSyncJobMap(List<SyncJobDef> syncJobDefs) {
		syncJobDefs.forEach(syncJob -> {

			/* All Jobs */
			syncJobMap.put(syncJob.getId(), syncJob);

			/* Active Jobs Map */
			if (syncJob.getIsActive()) {
				syncActiveJobMap.put(syncJob.getId(), syncJob);
			}
		});
	}

	private void updateJobs(final List<SyncJobDef> syncJobDefs) {

		jobConfigDAO.updateAll(syncJobDefs);

		/* Refresh The sync job map and sync active job map as we have updated jobs */
		setSyncJobMap(syncJobDefs);

	}

	private void executeMissedTrigger(final String jobId, final String syncFrequency) {

		ExecutionTime executionTime = getExecutionTime(syncFrequency);

		Instant last = getLast(executionTime);
		Instant next = getNext(executionTime);

		/* Check last and next has values present */
		if (last != null && next != null) {

			/* Get all Transactions in between last and next crDtimes */
			List<SyncTransaction> syncTransactions = syncJobTransactionDAO.getAll(jobId, Timestamp.from(last),
					Timestamp.from(next));

			/* Execute the Job if it was not started on previous pre-scheduled time */
			if ((isNull(syncTransactions) || isEmpty(syncTransactions))) {
				executeJob(jobId, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

			}
		}

	}

	private Instant getLast(ExecutionTime executionTime) {
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("UTC"));

		Optional<ZonedDateTime> lastDate = executionTime.lastExecution(currentTime);
		Instant last = null;
		if (lastDate.isPresent()) {
			last = lastDate.get().toInstant();

		}

		return last;

	}

	private Instant getNext(ExecutionTime executionTime) {
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("UTC"));

		Optional<ZonedDateTime> nextDate = executionTime.nextExecution(currentTime);
		Instant next = null;
		if (nextDate.isPresent()) {
			next = nextDate.get().toInstant();

		}

		return next;

	}

	private ExecutionTime getExecutionTime(String syncFrequency) {
		return ExecutionTime.forCron(cronParser.parse(syncFrequency));
	}

	private void executeMissedTriggers(Map<String, SyncJobDef> map) {

		map.forEach((jobId, syncJob) -> {
			if (syncJob.getParentSyncJobId() == null && syncJob.getSyncFrequency() != null
					&& syncJob.getApiName() != null) {
				/* An A-sync task to complete missed trigger */
				new Thread(() -> executeMissedTrigger(jobId, syncJob.getSyncFrequency())).start();
			}

		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.JobConfigurationService#executeAllJobs()
	 */
	@Override
	public ResponseDTO executeAllJobs() {
		ResponseDTO responseDTO = new ResponseDTO();

		BaseJob.clearCompletedJobMap();

		List<String> failureJobs = new LinkedList<>();

		for (Entry<String, SyncJobDef> syncJob : syncActiveJobMap.entrySet()) {
			if ((syncJob.getValue().getParentSyncJobId() == null
					|| syncJob.getValue().getParentSyncJobId().equalsIgnoreCase("NULL"))
					&& syncJob.getValue().getApiName() != null) {

				ResponseDTO jobResponse = executeJob(syncJob.getKey(), RegistrationConstants.JOB_TRIGGER_POINT_USER);
				if (jobResponse.getErrorResponseDTOs() != null) {
					failureJobs.add(syncActiveJobMap.get(syncJob.getKey()).getName());
				}
			}
		}

		if (!isEmpty(failureJobs)) {
			setErrorResponse(responseDTO, failureJobs.toString().replace("[", "").replace("]", ""), null);
		}

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.config.JobConfigurationService#isRestart()
	 */
	@Override
	public ResponseDTO isRestart() {
		ResponseDTO responseDTO = new ResponseDTO();
		/* Fetch completed job map */
		Map<String, String> completedSyncJobMap = BaseJob.getCompletedJobMap();

		/* Compare with restart-able job list */
		for (String jobId : restartableJobList) {

			/* Check the job completed with success/failure */
			if (RegistrationConstants.JOB_EXECUTION_SUCCESS.equals(completedSyncJobMap.get(jobId))) {

				/* Store job info in attributes of response */
				Map<String, Object> successJobAttribute = new WeakHashMap<>();
				successJobAttribute.put(RegistrationConstants.JOB_ID, jobId);

				return setSuccessResponse(responseDTO,
						syncActiveJobMap.get(jobId).getName() + " " + RegistrationConstants.OTP_VALIDATION_SUCCESS,
						successJobAttribute);
			}
		}

		return responseDTO;
	}

	@Override
	public ResponseDTO getRestartTime() {

		ResponseDTO responseDTO = new ResponseDTO();

		String syncDataFreq = getGlobalConfigValueOf(RegistrationConstants.SYNC_DATA_FREQ);
		if (syncDataFreq != null) {
			ExecutionTime executionTime = getExecutionTime(syncDataFreq);
			Instant last = getLast(executionTime);
			Instant next = getNext(executionTime);

			if (last != null && next != null) {
				setSuccessResponse(responseDTO, String.valueOf((Duration.between(last, next).toMillis()) / 5), null);
			}
		}
		return responseDTO;
	}

	/**
	 * scheduler factory bean used to schedule the batch jobs
	 * 
	 * @return scheduler factory which includes job detail and trigger detail
	 * @throws Exception
	 */
	private SchedulerFactoryBean getSchedulerFactoryBean(String count) throws Exception {
		SchedulerFactoryBean schFactoryBean = new SchedulerFactoryBean();
		schFactoryBean.setGlobalTriggerListeners(new TriggerListener[] { commonTriggerListener });
		schFactoryBean.setGlobalJobListeners(new JobListener[] { jobProcessListener });
		Properties quartzProperties = new Properties();
		quartzProperties.put("org.quartz.threadPool.threadCount", count);
		schFactoryBean.setQuartzProperties(quartzProperties);
		schFactoryBean.afterPropertiesSet();
		return schFactoryBean;
	}

	@Override
	public boolean isSchedulerRunning() {
		return isSchedulerRunning;
	}

	@Override
	public Map<String, SyncJobDef> getActiveSyncJobMap() {
		return syncActiveJobMap;
	}

}
