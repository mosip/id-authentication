package io.mosip.registration.test.jobs;

import static org.mockito.Mockito.doNothing;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.springframework.scheduling.TriggerContext;

import io.mosip.registration.jobs.JobProcessListener;
import io.mosip.registration.jobs.JobTriggerListener;
import io.mosip.registration.manager.BaseTransactionManager;

public class JobTriggerTest {
	
	@Mock
	BaseTransactionManager transactionManager;	

	@Mock
	Trigger trigger;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	JobExecutionContext jobExecutionContext;
	
	CompletedExecutionInstruction completedExecutionInstruction;
	
	@InjectMocks
	JobTriggerListener jobTriggerListener;
	
	@Test
	public void triggerMisFiredTest() {
		doNothing().when(transactionManager).createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		jobTriggerListener.triggerMisfired(trigger);

	}
	
	@Test
	public void triggerCompleteTest() {
		doNothing().when(transactionManager).createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		jobTriggerListener.triggerComplete(trigger, jobExecutionContext, completedExecutionInstruction);

	}
	
	@Test
	public void triggerFiredTest() {
		doNothing().when(transactionManager).createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		jobTriggerListener.triggerFired(trigger, jobExecutionContext);

	}
}
