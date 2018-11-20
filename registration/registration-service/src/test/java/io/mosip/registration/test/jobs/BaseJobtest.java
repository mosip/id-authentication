package io.mosip.registration.test.jobs;

import java.util.HashMap;
import java.util.LinkedList;

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
import org.springframework.context.ApplicationContext;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJob;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.jobs.impl.PacketSyncStatusJob;
import io.mosip.registration.manager.BaseTransactionManager;
import io.mosip.registration.service.RegPacketStatusService;
import io.mosip.registration.service.impl.JobConfigurationServiceImpl;

public class BaseJobtest {

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	BaseTransactionManager transactionManager;

	@Mock
	JobExecutionContext context;

	@Mock
	JobDetail jobDetail;

	@Mock
	JobDataMap jobDataMap;
	@InjectMocks
	PacketSyncStatusJob packetSyncStatusJob;

	@Mock
	BaseJob baseJob;

	@Mock
	RegPacketStatusService packetStatusService;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private LinkedList<SyncJob> syncJobList;
	HashMap<String, SyncJob> jobMap = new HashMap<>();

	@Before
	public void intiate() {
		syncJobList = new LinkedList<>();
		SyncJob syncJob = new SyncJob();
		syncJob.setId("1");

		syncJob.setApiName("packetSyncStatusJob");
		syncJob.setSyncFrequency("0/5 * * * * ?");
		syncJobList.add(syncJob);

		syncJobList.forEach(job -> {
			jobMap.put(job.getId(), job);
		});
		JobConfigurationServiceImpl.SYNC_JOB_MAP = jobMap;

	}

	@Test
	public void executeinternalTest() throws JobExecutionException {

		SyncJob syncJob = new SyncJob();
		syncJob.setId("1");
		ResponseDTO responseDTO = new ResponseDTO();

		Mockito.when(context.getJobDetail()).thenReturn(jobDetail);
		Mockito.when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		Mockito.when(jobDataMap.get(Mockito.any())).thenReturn(applicationContext);
		Mockito.when(applicationContext.getBean(BaseTransactionManager.class)).thenReturn(transactionManager);
		Mockito.when(applicationContext.getBean(RegPacketStatusService.class)).thenReturn(packetStatusService);
		
		Mockito.when(transactionManager.getJob(context)).thenReturn(syncJob);
		Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(packetSyncStatusJob);
	
		Mockito.when(packetStatusService.packetSyncStatus()).thenReturn(responseDTO);

	
		packetSyncStatusJob.executeInternal(context);

	}
	


	@Test
	public void executejobTest() {
		ResponseDTO responseDTO=new ResponseDTO();
		ErrorResponseDTO  errorResponseDTO=new ErrorResponseDTO();
		errorResponseDTO.setCode("ERROR");
		LinkedList<ErrorResponseDTO> errorResponseDTOs=new LinkedList<>();
		errorResponseDTOs.add(errorResponseDTO);
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		Mockito.when(applicationContext.getBean(BaseTransactionManager.class)).thenReturn(transactionManager);
		Mockito.when(applicationContext.getBean(RegPacketStatusService.class)).thenReturn(packetStatusService);
		//Mockito.when(JobConfigurationServiceImpl.SYNC_JOB_MAP.get(Mockito.any())).thenReturn(new SyncJob());
		Mockito.when(transactionManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		
		Mockito.when(packetStatusService.packetSyncStatus()).thenReturn(responseDTO);
		packetSyncStatusJob.executeJob("User");
	}
	
	
	@Test
	public void executejobExceptionTest() {
		ResponseDTO responseDTO=new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		Mockito.when(applicationContext.getBean(BaseTransactionManager.class)).thenReturn(transactionManager);
		Mockito.when(applicationContext.getBean(RegPacketStatusService.class)).thenReturn(packetStatusService);
		//Mockito.when(JobConfigurationServiceImpl.SYNC_JOB_MAP.get(Mockito.any())).thenReturn(new SyncJob());
		Mockito.when(transactionManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		
		Mockito.when(packetStatusService.packetSyncStatus()).thenReturn(responseDTO);
		packetSyncStatusJob.executeJob("User");
	}

}
