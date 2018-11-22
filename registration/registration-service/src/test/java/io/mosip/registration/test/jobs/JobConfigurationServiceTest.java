package io.mosip.registration.test.jobs;

import static org.mockito.Mockito.doNothing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.impl.PacketSyncStatusJob;
import io.mosip.registration.service.impl.JobConfigurationServiceImpl;

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

	List<SyncJobDef> syncJobList;

	HashMap<String, SyncJobDef> jobMap = new HashMap<>();

	@Before
	public void intiate() {
		syncJobList = new LinkedList<>();
		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("1234");

		syncJob.setApiName("packetSyncStatusJob");
		syncJob.setSyncFrequency("0/5 * * * * ?");
		syncJobList.add(syncJob);

		syncJobList.forEach(job -> {
			jobMap.put(job.getId(), job);
		});
		JobConfigurationServiceImpl.SYNC_JOB_MAP=jobMap;

	}

	@Test
	public void initiateJobTest() {
		Mockito.when(jobConfigDAO.getAll()).thenReturn(syncJobList);
		jobConfigurationService.initiateJobs();

	}

	@Test
	public void startJobs() throws SchedulerException {
		intiate();
		BaseJob job = new PacketSyncStatusJob();

		// SchedulerFactoryBean
		// schedulerFactoryBean=Mockito.mock(SchedulerFactoryBean.class);
		// doNothing().when((schedulerFactoryBean)).getScheduler();//.scheduleJob(Mockito.any(),
		// Mockito.any()));
		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenReturn(new Date());
		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(job);
		jobConfigurationService.startJobs(applicationContext);
	}

	@Test
	public void startJobsShedulerExceptionTest() throws SchedulerException {
		BaseJob job = new PacketSyncStatusJob();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenThrow(SchedulerException.class);
		
		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(job);
		jobConfigurationService.startJobs(applicationContext);
	}

	@Test
	public void startJobsParseExceptionTest() throws SchedulerException {
		BaseJob job = new PacketSyncStatusJob();

		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenThrow(ParseException.class);
		initiateJobTest();
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(job);
		jobConfigurationService.startJobs(applicationContext);
	}
	
	@Test
	public void stopJobsTest() throws SchedulerException {
		
		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		doNothing().when(scheduler).shutdown();
		jobConfigurationService.stopJobs();
	}
	
	@Test
	public void stopJobsExceptionTest() throws SchedulerException {
		Mockito.when(schedulerFactoryBean.getScheduler()).thenThrow(SchedulerException.class);
		jobConfigurationService.stopJobs();
	}

	@Test
	public void getCurrentRunningJobDetailsTest() throws SchedulerException {
		List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();
		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobExecutionContexts);
		jobConfigurationService.getCurrentRunningJobDetails();
	}
	
	@Test
	public void getCurrentRunningJobDetailsExceptionTest() throws SchedulerException {
		Mockito.when(schedulerFactoryBean.getScheduler()).thenThrow(SchedulerException.class);
		jobConfigurationService.getCurrentRunningJobDetails();
	}
	
	@Test
	public void executeJobJobTest() throws SchedulerException {
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(packetSyncJob);
		Mockito.when(packetSyncJob.executeJob(Mockito.anyString())).thenReturn(new ResponseDTO());
		jobConfigurationService.executeJob(applicationContext, "packetSyncStatusJob");
	}
	@Test
	public void executeJobExceptionJobTest() throws SchedulerException {
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);
		jobConfigurationService.executeJob(applicationContext, "packetSyncStatusJob");
	}
}
