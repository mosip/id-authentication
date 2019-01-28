package io.mosip.registration.test.jobs;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncTransactionDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.impl.PacketSyncStatusJob;
import io.mosip.registration.service.config.impl.JobConfigurationServiceImpl;

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

		syncJobList.forEach(job -> {
			jobMap.put(job.getId(), job);
		});
		Mockito.when(jobConfigDAO.getActiveJobs()).thenReturn(syncJobList);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(syncJobList);

	}

	@Test
	public void initiateJobTest() {
		jobConfigurationService.initiateJobs();

	}

	@Test
	public void startJobs() throws SchedulerException {
		BaseJob job = new PacketSyncStatusJob();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenReturn(new Date());

		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(job);
		assertSame(RegistrationConstants.BATCH_JOB_START_SUCCESS_MESSAGE,
				jobConfigurationService.startScheduler(applicationContext).getSuccessResponseDTO().getMessage());
	}

	@Test
	public void startJobsShedulerExceptionTest() throws SchedulerException {
		BaseJob job = new PacketSyncStatusJob();

		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		doNothing().when(scheduler).clear();
		initiateJobTest();
		assertSame(RegistrationConstants.START_SCHEDULER_ERROR_MESSAGE,
				jobConfigurationService.startScheduler(applicationContext).getErrorResponseDTOs().get(0).getMessage());

	}

	@Test
	public void startJobsShedulerExceptionTest2() throws SchedulerException {
		BaseJob job = new PacketSyncStatusJob();

		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);
		// Mockito.when(scheduler.scheduleJob(Mockito.any(),
		// Mockito.any())).thenThrow(SchedulerException.class);

		Mockito.when(schedulerFactoryBean.getScheduler()).thenThrow(SchedulerException.class);
		initiateJobTest();
		// Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(job);
		// jobConfigurationService.startScheduler(applicationContext);

	}

	@Test
	public void stopJobsTest() throws SchedulerException {

		Mockito.when(schedulerFactoryBean.isRunning()).thenReturn(true);
		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		doNothing().when(scheduler).clear();

		doNothing().when(schedulerFactoryBean).stop();
		jobConfigurationService.stopScheduler();

		Mockito.when(schedulerFactoryBean.isRunning()).thenReturn(false);
		jobConfigurationService.stopScheduler();

	}

	@Test
	public void stopJobsExceptionTest() throws SchedulerException {
		Mockito.when(schedulerFactoryBean.isRunning()).thenThrow(RuntimeException.class);
		jobConfigurationService.stopScheduler();

	}

	@Test
	public void getCurrentRunningJobDetailsTest() throws SchedulerException {
		initiateJobTest();
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();
		jobExecutionContexts.add(jobExecutionContext);

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobExecutionContexts);
		Mockito.when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
		Mockito.when(jobDetail.getKey()).thenReturn(new JobKey("1234"));

		jobConfigurationService.getCurrentRunningJobDetails();
	}

	@Test
	public void getCurrentRunningJobDetailsEmptyTest() throws SchedulerException {
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobExecutionContexts);
		jobConfigurationService.getCurrentRunningJobDetails();
	}

	@Test
	public void getCurrentRunningJobDetailsExceptionTest() throws SchedulerException {
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenThrow(SchedulerException.class);
		jobConfigurationService.getCurrentRunningJobDetails();
	}

	@Test
	public void executeJobTest() throws SchedulerException {
		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(packetSyncJob);
		Mockito.when(packetSyncJob.executeJob(Mockito.anyString(), Mockito.anyString())).thenReturn(new ResponseDTO());
		jobConfigurationService.executeJob(applicationContext, "1234");
	}

	@Test
	public void executeJobExceptionJobTest() throws SchedulerException {
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);
		jobConfigurationService.executeJob(applicationContext, "packetSyncStatusJob");
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

		Timestamp req = new Timestamp(System.currentTimeMillis());
		Mockito.when(syncJobTransactionDAO.getSyncTransactions(Mockito.any())).thenReturn(syncTransactions);
		Assert.assertNotNull(jobConfigurationService.getSyncJobsTransaction().getSuccessResponseDTO());

		syncTransactions.clear();
		Mockito.when(syncJobTransactionDAO.getSyncTransactions(Mockito.any())).thenReturn(syncTransactions);
		Assert.assertNotNull(jobConfigurationService.getSyncJobsTransaction().getErrorResponseDTOs());

	}

}
