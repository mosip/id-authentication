package io.mosip.registration.test.jobs;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncTransactionDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.impl.PacketSyncStatusJob;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.config.impl.JobConfigurationServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({  io.mosip.registration.jobs.BaseJob.class })
public class JobConfigurationServiceTest {

	@Mock
	private SyncJobConfigDAO jobConfigDAO;

	@Mock
	private ApplicationContext applicationContext;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	PacketSyncStatusJob packetSyncJob;

	@InjectMocks
	private JobConfigurationServiceImpl jobConfigurationService;

	@Mock
	SchedulerFactoryBean schedulerFactoryBean;

	@Mock
	Scheduler scheduler;

	@Mock
	SyncJobControlDAO syncJobDAO;

	@Mock
	SyncTransactionDAO syncJobTransactionDAO;

	@Mock
	JobExecutionContext jobExecutionContext;

	@Mock
	JobDetail jobDetail;

	@Mock
	io.mosip.registration.context.ApplicationContext context;

	@Mock
	GlobalParamDAO globalParamDAO;

	@Mock
	GlobalParamService globalParamService;

	List<SyncJobDef> syncJobList;

	HashMap<String, SyncJobDef> jobMap = new HashMap<>();



	@Before
	public void intiate() {
		syncJobList = new LinkedList<>();
		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("1234");

		syncJob.setApiName("packetSyncStatusJob");
		syncJob.setSyncFrequency("0/5 * * * * ?");
		syncJob.setIsActive(true);
		syncJobList.add(syncJob);

		SyncJobDef mdsJob = new SyncJobDef();
		mdsJob.setId("RCS_J00005");

		mdsJob.setApiName("packetSyncStatusJob");
		mdsJob.setSyncFrequency("0/5 * * * * ?");
		mdsJob.setIsActive(true);
		mdsJob.setName("Master Data Sync");
		syncJobList.add(mdsJob);

		syncJobList.forEach(job -> {
			jobMap.put(job.getId(), job);
		});
		Mockito.when(jobConfigDAO.getActiveJobs()).thenReturn(syncJobList);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(syncJobList);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.SYNC_TRANSACTION_NO_OF_DAYS_LIMIT, "5");
		applicationMap.put(RegistrationConstants.SYNC_DATA_FREQ, "0 0 11 * * ?");

//		PowerMockito.mockStatic(io.mosip.registration.context.ApplicationContext.class);
//		when(io.mosip.registration.context.ApplicationContext.map()).thenReturn(applicationMap);
//		PowerMockito.mockStatic(io.mosip.registration.context.ApplicationContext.class);
//		when(io.mosip.registration.context.ApplicationContext.getInstance()).thenReturn(context);
//		Map<String, Object> map = new HashMap<>();
//		map.put(RegistrationConstants.SYNC_TRANSACTION_NO_OF_DAYS_LIMIT, "5");

//		Mockito.when(globalParamService.getGlobalParams()).thenReturn(map);

		io.mosip.registration.context.ApplicationContext.setApplicationMap(applicationMap);
			}

	@Test
	public void startJobs() throws SchedulerException {
		// BaseJob job = new PacketSyncStatusJob();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenReturn(new Date());

		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(packetSyncJob);
		Mockito.when(packetSyncJob.jobClass()).thenReturn(PacketSyncStatusJob.class);
		Mockito.when(syncJobTransactionDAO.getAll(Mockito.anyString(), new Timestamp(Mockito.anyLong()),
				new Timestamp(Mockito.anyLong()))).thenReturn(new LinkedList<>());

		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		Mockito.when(packetSyncJob.executeJob(Mockito.anyString(), Mockito.anyString())).thenReturn(responseDTO);

		assertSame(RegistrationConstants.BATCH_JOB_START_SUCCESS_MESSAGE,
				jobConfigurationService.startScheduler().getSuccessResponseDTO().getMessage());
	}

	@Test
	public void initiateJobTest() {
		GlobalParam globalParam = new GlobalParam();
		globalParam.setName("1234");
		globalParam.setVal("0/10 * * * * ?");
		globalParam.setIsActive(true);
		List<GlobalParam> globalParams = new LinkedList<>();
		globalParams.add(globalParam);

		List<SyncJobDef> updatedJobs = new LinkedList<>();
		SyncJobDef syncJobDef = new SyncJobDef();
		syncJobDef.setId(globalParam.getName());
		syncJobDef.setSyncFrequency(globalParam.getVal());
		syncJobDef.setIsActive(globalParam.getIsActive());
		syncJobDef.setApiName("packetSyncStatusJob");
		updatedJobs.add(syncJobDef);

		Mockito.when(globalParamDAO.getAll(Mockito.anyList())).thenReturn(globalParams);
		Mockito.when(jobConfigDAO.updateAll(Mockito.anyList())).thenReturn(updatedJobs);

		jobConfigurationService.initiateJobs();

	}

	@Test
	public void startJobsShedulerExceptionTest() throws SchedulerException {

		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		doNothing().when(scheduler).clear();
		initiateJobTest();
		assertSame(RegistrationConstants.START_SCHEDULER_ERROR_MESSAGE,
				jobConfigurationService.startScheduler().getErrorResponseDTOs().get(0).getMessage());

	}

	@Test
	public void stopJobsTest() throws SchedulerException {

		Mockito.when(schedulerFactoryBean.isRunning()).thenReturn(true);
		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		doNothing().when(scheduler).clear();

		doNothing().when(schedulerFactoryBean).stop();
		Assert.assertSame(RegistrationConstants.BATCH_JOB_STOP_SUCCESS_MESSAGE,
				jobConfigurationService.stopScheduler().getSuccessResponseDTO().getMessage());

		Mockito.when(schedulerFactoryBean.isRunning()).thenReturn(false);
		Assert.assertSame(RegistrationConstants.SYNC_DATA_PROCESS_ALREADY_STOPPED,
				jobConfigurationService.stopScheduler().getErrorResponseDTOs().get(0).getMessage());

	}

	@Test
	public void stopJobsRunTimeExceptionTest() throws SchedulerException {
		Mockito.when(schedulerFactoryBean.isRunning()).thenThrow(RuntimeException.class);
		Assert.assertSame(RegistrationConstants.STOP_SCHEDULER_ERROR_MESSAGE,
				jobConfigurationService.stopScheduler().getErrorResponseDTOs().get(0).getMessage());

	}

	@Test
	public void stopJobsSchedulerExceptionTest() throws SchedulerException {
		Mockito.when(schedulerFactoryBean.isRunning()).thenThrow(SchedulerException.class);
		Assert.assertSame(RegistrationConstants.STOP_SCHEDULER_ERROR_MESSAGE,
				jobConfigurationService.stopScheduler().getErrorResponseDTOs().get(0).getMessage());

	}

	@Test
	public void getCurrentRunningJobDetailsTest() throws SchedulerException {
		startJobs();
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();
		jobExecutionContexts.add(jobExecutionContext);

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobExecutionContexts);
		Mockito.when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
		Mockito.when(jobDetail.getKey()).thenReturn(new JobKey("1234"));

		Assert.assertNotNull(jobConfigurationService.getCurrentRunningJobDetails().getSuccessResponseDTO());
	}

	@Test
	public void getCurrentRunningJobDetailsEmptyTest() throws SchedulerException {
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobExecutionContexts);
		Assert.assertNotNull(jobConfigurationService.getCurrentRunningJobDetails().getErrorResponseDTOs());

	}

	@Test
	public void getCurrentRunningJobDetailsExceptionTest() throws SchedulerException {
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();

		startJobs();
		Mockito.when(schedulerFactoryBean.getScheduler()).thenThrow(SchedulerException.class);
		Assert.assertSame(RegistrationConstants.CURRENT_JOB_DETAILS_ERROR_MESSAGE,
				jobConfigurationService.getCurrentRunningJobDetails().getErrorResponseDTOs().get(0).getMessage());
	}

	@Test
	public void executeJobTest() throws SchedulerException {
		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(packetSyncJob);
		Mockito.when(packetSyncJob.executeJob(Mockito.anyString(), Mockito.anyString())).thenReturn(new ResponseDTO());
		Assert.assertNotNull(
				jobConfigurationService.executeJob("1234", RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM));
	}

	@Test
	public void executeJobExceptionJobTest() throws SchedulerException {
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);
		Assert.assertNotNull(jobConfigurationService.executeJob("packetSyncStatusJob",
				RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM));
	}

	@Test
	public void executeJobRunTimeExceptionJobTest() throws SchedulerException {
		Mockito.when(packetSyncJob.executeJob(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(NoSuchBeanDefinitionException.class);
		jobConfigurationService.executeJob("packetSyncStatusJob", RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
	}

	@Test
	public void getLastCompletedSyncJobsTest() {
		initiateJobTest();
		List<SyncControl> syncControls = new LinkedList<>();
		SyncControl syncControl = new SyncControl();
		syncControl.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		syncControl.setId("1234");
		syncControl.setSyncJobId("1234");

		syncControls.add(syncControl);
		Mockito.when(syncJobDAO.findAll()).thenReturn(syncControls);
		Assert.assertNotNull(jobConfigurationService.getLastCompletedSyncJobs().getSuccessResponseDTO());

		syncControls.clear();

		Mockito.when(syncJobDAO.findAll()).thenReturn(syncControls);
		Assert.assertNotNull(jobConfigurationService.getLastCompletedSyncJobs().getErrorResponseDTOs());

	}

	@Test
	public void getSyncTransactionTest() {

		initiateJobTest();
		List<SyncTransaction> syncTransactions = new LinkedList<>();
		SyncTransaction syncTransaction = new SyncTransaction();
		syncTransaction.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		syncTransaction.setId("1234");
		syncTransaction.setSyncJobId("1234");
		syncTransaction.setStatusCode("Triggered");
		syncTransaction.setCrDtime(new Timestamp(System.currentTimeMillis()));

		syncTransactions.add(syncTransaction);

		Mockito.when(syncJobTransactionDAO.getSyncTransactions(Mockito.any(), Mockito.anyString()))
				.thenReturn(syncTransactions);

		Assert.assertNotNull(jobConfigurationService.getSyncJobsTransaction().getSuccessResponseDTO());

	}

	@Test
	public void isRestartTestSuccess() {
		initiateJobTest();
		HashMap<String, String> completedJobMap = new HashMap<>();
		completedJobMap.put("RCS_J00005", RegistrationConstants.JOB_EXECUTION_SUCCESS);

		PowerMockito.mockStatic(BaseJob.class);

		Mockito.when(BaseJob.getCompletedJobMap()).thenReturn(completedJobMap);
		Assert.assertNotNull(jobConfigurationService.isRestart().getSuccessResponseDTO());

	}

	@Test
	public void isRestartTestFailure() {
		initiateJobTest();
		HashMap<String, String> completedJobMap = new HashMap<>();

		PowerMockito.mockStatic(BaseJob.class);

		Mockito.when(BaseJob.getCompletedJobMap()).thenReturn(completedJobMap);
		Assert.assertNull(jobConfigurationService.isRestart().getErrorResponseDTOs());

	}

	@Test
	public void executeAllJobsTest() throws SchedulerException {

		ResponseDTO responseDTO = new ResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOs = new LinkedList<>();
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		initiateJobTest();
		
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(packetSyncJob);
		Mockito.when(packetSyncJob.executeJob(Mockito.anyString(), Mockito.anyString())).thenReturn(responseDTO);


		Assert.assertNotNull(jobConfigurationService.executeAllJobs());
	}

	@Test
	public void getRestartTimeTest() {
		Assert.assertNotNull(jobConfigurationService.getRestartTime().getSuccessResponseDTO());
	}
	
	@Test
	public void getActiveJobTest() {
		Assert.assertNotNull(jobConfigurationService.getActiveSyncJobMap());
	}@Test
	public void getOfflineJobsTest() {
		Assert.assertNotNull(jobConfigurationService.getOfflineJobs());
	}@Test
	public void getUnTaggedJobsTest() {
		Assert.assertNotNull(jobConfigurationService.getUnTaggedJobs());
	}
	
	@Test
	public void startSchedulerExcepTest() {
		
		Mockito.doThrow(RuntimeException.class).when(schedulerFactoryBean).start();
		
		Assert.assertNotNull(jobConfigurationService.startScheduler().getErrorResponseDTOs());
		
		
		Assert.assertNotNull(jobConfigurationService.getUnTaggedJobs());
	}
}
