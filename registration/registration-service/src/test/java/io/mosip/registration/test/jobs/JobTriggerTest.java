package io.mosip.registration.test.jobs;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;

import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.JobManager;
import io.mosip.registration.jobs.JobTriggerListener;
import io.mosip.registration.jobs.SyncManager;

public class JobTriggerTest {
	
	@Mock
	SyncManager transactionManager;	

	@Mock
	Trigger trigger;
	
	@Mock
	JobManager jobManager;
	
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	JobExecutionContext jobExecutionContext;
	
	@Mock
	SyncTransaction syncTransaction;
	CompletedExecutionInstruction completedExecutionInstruction;
	
	@InjectMocks
	JobTriggerListener jobTriggerListener;
	
	@Before
	public void initiateTest() {
		Mockito.when(jobManager.getJobId(jobExecutionContext)).thenReturn("1");
	}
	
	
	@Test
	public void triggerMisFiredTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(syncTransaction);
		jobTriggerListener.triggerMisfired(trigger);

	}
	
	@Test
	public void triggerCompleteTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(syncTransaction);
		jobTriggerListener.triggerComplete(trigger, jobExecutionContext, completedExecutionInstruction);

	}
	
	@Test
	public void triggerFiredTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(syncTransaction);
		jobTriggerListener.triggerFired(trigger, jobExecutionContext);

	}
	
	@Test
	public void triggerMisFiredExceptionTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		jobTriggerListener.triggerMisfired(trigger);

	}
	
	@Test
	public void triggerCompleteExceptionTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		jobTriggerListener.triggerComplete(trigger, jobExecutionContext, completedExecutionInstruction);

	}
	
	@Test
	public void triggerFiredExceptionTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		jobTriggerListener.triggerFired(trigger, jobExecutionContext);

	}
}
