package io.mosip.registration.service.config.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
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

	/**
	 * To Fetch Job Configuration details
	 */
	@Autowired
	private SyncJobConfigDAO jobConfigDAO;

	/**
	 * Scheduler factory bean which will take Job and Trigger details and run jobs
	 * implicitly
	 */
	@Autowired
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
	 * To fetch required global params
	 */
	@Autowired
	private GlobalParamDAO globalParamDAO;

	/**
	 * Base Job
	 */
	private BaseJob baseJob;

	/**
	 * create a parser based on provided definition
	 */
	private static CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));

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
			/* Get All Jobs */
			List<SyncJobDef> jobDefs = getJobs();

			if (!isNull(jobDefs) && !isEmpty(jobDefs)) {

				/* Set Job-map and active sync-job-map */
				setSyncJobMap(jobDefs);

				List<String> names = jobDefs.stream().map(syncJobDef -> {
					return syncJobDef.getId();
				}).collect(Collectors.toList());

				/* Get Job Values from Global_Param for all jobs */
				List<GlobalParam> globalParams = globalParamDAO.getAll(names);

				if (!isNull(globalParams) && !isEmpty(globalParams)) {

					/* Update Jobs Using global_Params and refresh the job maps */
					updateJobsFromGlobalParam(globalParams);

				}
			}

			/* Check and Execute missed triggers */
			executeMissedTriggers(syncActiveJobMap);
			
			/* Start Scheduler */
			startScheduler();

		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

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
		if (isSchedulerRunning) {
			return setErrorResponse(responseDTO, RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STARTED, null);
		} else {
			schedulerFactoryBean.start();
			isSchedulerRunning = true;

			/* Job Data Map */
			Map<String, Object> jobDataAsMap = new HashMap<>();
			jobDataAsMap.put("applicationContext", applicationContext);
			jobDataAsMap.putAll(syncJobMap);

			jobDataMap = new JobDataMap(jobDataAsMap);

			loadScheduler(responseDTO);

		}

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "start jobs invocation ended");

		return responseDTO;

	}

	private void loadScheduler(ResponseDTO responseDTO) {
		syncActiveJobMap.forEach((jobId, syncJob) -> {
			try {
				if (syncJob.getParentSyncJobId() == null && syncJob.getApiName()!=null && responseDTO.getErrorResponseDTOs() == null
						&& isSchedulerRunning && !schedulerFactoryBean.getScheduler().checkExists(new JobKey(jobId))) {

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
						RegistrationConstants.APPLICATION_ID, exception.getMessage());

				/* Stop, Clear Scheduler and set Error response */
				setStartExceptionError(responseDTO);

			} catch (RuntimeException runtimeException) {
				LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
				setStartExceptionError(responseDTO);

			}

			if (isSchedulerRunning) {
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
					RegistrationConstants.APPLICATION_ID, schedulerException.getMessage());
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
		} catch (SchedulerException schedulerException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, schedulerException.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.STOP_SCHEDULER_ERROR_MESSAGE, null);

		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
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
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, schedulerException.getMessage());

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
	 * lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String jobId) {

		LOGGER.info(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Execute job started");
		ResponseDTO responseDTO = null;
		try {

			SyncJobDef syncJobDef = syncActiveJobMap.get(jobId);

			if (syncJobDef != null && syncJobDef.getApiName()!=null) {
				// Get Job using application context and api name
				baseJob = (BaseJob) applicationContext.getBean(syncJobDef.getApiName());

				String triggerPoint = getUserIdFromSession() != null ? getUserIdFromSession()
						: RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;

				// Job Invocation
				responseDTO = baseJob.executeJob(triggerPoint, jobId);
			} else {
				responseDTO = new ResponseDTO();
				setErrorResponse(responseDTO, RegistrationConstants.EXECUTE_JOB_ERROR_MESSAGE, null);
			}

		} catch (NoSuchBeanDefinitionException | NullPointerException | IllegalArgumentException exception) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());

			responseDTO = new ResponseDTO();
			setErrorResponse(responseDTO, RegistrationConstants.EXECUTE_JOB_ERROR_MESSAGE, null);
		} catch(RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.BATCH_JOBS_CONFIG_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

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
			cal.setTime(new Timestamp(System.currentTimeMillis()));
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
			HashMap<String, Object> attributes = new HashMap<>();
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

	private void updateJobsFromGlobalParam(final List<GlobalParam> globalParams) {

		/* Jobs to be updated */
		List<SyncJobDef> jobsToBeUpdated = new LinkedList<>();

		globalParams.forEach(globalParam -> {

			/* Check the global param's value is valid or not */
			if (globalParam.getVal() != null && syncJobMap.containsKey(globalParam.getName())) {
				SyncJobDef syncJobDef = syncJobMap.get(globalParam.getName());

				/* check whether the job has any new value to be updated */
				if (syncJobDef.getSyncFrequency() == null
						|| !(syncJobDef.getSyncFrequency().equals(globalParam.getVal()))
						|| !(syncJobDef.getIsActive().equals(globalParam.getIsActive()))) {

					syncJobDef.setSyncFrequency(globalParam.getVal());
					syncJobDef.setIsActive(globalParam.getIsActive());
					syncJobDef.setUpdBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
					syncJobDef.setUpdDtimes(Timestamp.valueOf(LocalDateTime.now()));

					jobsToBeUpdated.add(syncJobDef);

				}

			}
		});

		if (!isEmpty(jobsToBeUpdated)) {
			/* Update The Sync Jobs */
			List<SyncJobDef> updatedJobs = updateJobs(jobsToBeUpdated);

			/* Refresh The sync job map and sync active job map as we have updated jobs */
			setSyncJobMap(updatedJobs);
		}

	}

	private List<SyncJobDef> updateJobs(final List<SyncJobDef> syncJobDefs) {
		return jobConfigDAO.updateAll(syncJobDefs);
	}

	private void executeMissedTrigger(final String jobId, final String syncFrequency) {

		ExecutionTime executionTime = ExecutionTime.forCron(cronParser.parse(syncFrequency));

		ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.systemDefault());

		Optional<ZonedDateTime> last = executionTime.lastExecution(currentTime);
		Optional<ZonedDateTime> next = executionTime.nextExecution(currentTime);

		/* Check last and next has values present */
		if (last.isPresent() && next.isPresent()) {

			/* Get all Transactions in between last and next crDtimes */
			List<SyncTransaction> syncTransactions = syncJobTransactionDAO.getAll(jobId,
					Timestamp.from(last.get().toInstant()), Timestamp.from(next.get().toInstant()));

			/* Execute the Job if it was not started on previous pre-scheduled time */
			if ((isNull(syncTransactions) || isEmpty(syncTransactions))
					&& (executeJob(jobId).getSuccessResponseDTO() != null)) {
				baseJob.setApplicationContext(applicationContext);

				/* Execute all its child jobs */
				baseJob.executeChildJob(jobId, syncJobMap);

			}
		}

	}

	private void executeMissedTriggers(Map<String, SyncJobDef> map) {

		map.forEach((jobId, syncJob) -> {
			if (syncJob.getParentSyncJobId() == null && syncJob.getSyncFrequency() != null) {
				/* An Async task to complete missed trigger */
				new Thread(() -> executeMissedTrigger(jobId, syncJob.getSyncFrequency())).start();
			}

		});

	}
}
