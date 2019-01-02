package io.mosip.registration.test.jobs;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.JobManager;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.jobs.impl.PreRegistrationPacketDeletionJob;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;

public class PreRegistrationPacketDeletionJobTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	PreRegistrationDataSyncService preRegistrationDataSyncService;
	
	@Mock
	private ApplicationContext applicationContext;

	@Mock
	SyncManager syncManager;
	
	@Mock
	private SyncJobConfigDAO jobConfigDAO;
	
	@Mock
	JobManager jobManager;

	@Mock
	JobExecutionContext context;

	@Mock
	JobDetail jobDetail;

	@Mock
	JobDataMap jobDataMap;
	
	HashMap<String, SyncJobDef> jobMap = new HashMap<>();
	
	@Mock
	BaseJob baseJob;
	
	@InjectMocks
	PreRegistrationPacketDeletionJob deletionJob;
	private LinkedList<SyncJobDef> syncJobList;
	
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
		Mockito.when(jobConfigDAO.getActiveJobs()).thenReturn(syncJobList);
		
	}

	@Test
	public void executeinternalTest() throws JobExecutionException {

		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("1");
		
		Map<String, SyncJobDef> jobMap=new HashMap<>();
		
		jobMap.put(syncJob.getId(), syncJob);
		
		syncJob.setId("2");
		syncJob.setParentSyncJobId("1");
		
		
		jobMap.put("2", syncJob);
		
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		Mockito.when(context.getJobDetail()).thenReturn(jobDetail);
		Mockito.when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		Mockito.when(jobDataMap.get(Mockito.any())).thenReturn(applicationContext);
		Mockito.when(applicationContext.getBean(SyncManager.class)).thenReturn(syncManager);
		Mockito.when(applicationContext.getBean(JobManager.class)).thenReturn(jobManager);
		Mockito.when(applicationContext.getBean(PreRegistrationDataSyncService.class)).thenReturn(preRegistrationDataSyncService);
		
		Mockito.when(jobManager.getChildJobs(Mockito.any())).thenReturn(jobMap);
		Mockito.when(jobManager.getJobId(Mockito.any(JobExecutionContext.class))).thenReturn("1");
		
		
		
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(deletionJob);
	
		Mockito.when(preRegistrationDataSyncService.fetchAndDeleteRecords()).thenReturn(responseDTO);

	
		deletionJob.executeInternal(context);
		deletionJob.executeJob("User", "1");

	}
	


	
	
	@Test(expected = RegBaseUncheckedException.class)
	public void executejobNoSuchBeanDefinitionExceptionTest() {
		ResponseDTO responseDTO=new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);
//		Mockito.when(applicationContext.getBean(SyncManager.class)).thenThrow(NoSuchBeanDefinitionException.class);
//				preRegistrationDataSyncJob.executeJob("User");
//				
		Mockito.when(context.getJobDetail()).thenThrow(NoSuchBeanDefinitionException.class);
	deletionJob.executeInternal(context);
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void executejobNullPointerExceptionTest() {
		ResponseDTO responseDTO=new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		Mockito.when(context.getJobDetail()).thenThrow(NullPointerException.class);
		
	deletionJob.executeInternal(context);
	}
	
	@Test
	public void executejobRunTimeExceptionTest() {
		ResponseDTO responseDTO=new ResponseDTO();
		ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOs=new ArrayList<>();
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("1");
		
		Map<String, SyncJobDef> jobMap=new HashMap<>();
		
		jobMap.put(syncJob.getId(), syncJob);
		
		syncJob.setId("2");
		syncJob.setParentSyncJobId("1");
		
		
		jobMap.put("2", syncJob);

		Mockito.when(context.getJobDetail()).thenReturn(jobDetail);
		Mockito.when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		Mockito.when(jobDataMap.get(Mockito.any())).thenReturn(applicationContext);
		Mockito.when(applicationContext.getBean(SyncManager.class)).thenReturn(syncManager);
		Mockito.when(applicationContext.getBean(JobManager.class)).thenReturn(jobManager);
		Mockito.when(applicationContext.getBean(PreRegistrationDataSyncService.class)).thenReturn(preRegistrationDataSyncService);
		
		Mockito.when(jobManager.getChildJobs(Mockito.any())).thenReturn(jobMap);
		Mockito.when(jobManager.getJobId(Mockito.any(JobExecutionContext.class))).thenReturn("1");
		
		
		
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(deletionJob);
	
		Mockito.when(preRegistrationDataSyncService.fetchAndDeleteRecords()).thenThrow(RuntimeException.class);
		
		//assertEquals(responseDTO, deletionJob.executeInternal(context));
		deletionJob.executeInternal(context);
	
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void executeChildJobsTest() throws JobExecutionException {
		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("1");
		
		Map<String, SyncJobDef> jobMap=new HashMap<>();
		
		jobMap.put(syncJob.getId(), syncJob);
		
		syncJob.setId("2");
		syncJob.setParentSyncJobId("1");
		
		
		jobMap.put("2", syncJob);
		
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenThrow(NoSuchBeanDefinitionException.class);
		
		deletionJob.executeChildJob("1", jobMap);

	}
	
}
