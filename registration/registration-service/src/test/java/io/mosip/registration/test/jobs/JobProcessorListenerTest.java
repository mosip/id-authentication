package io.mosip.registration.test.jobs;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.JobProcessListener;
import io.mosip.registration.manager.SyncManager;
import static org.mockito.Mockito.doNothing;

public class JobProcessorListenerTest {
	
	@Mock
	SyncManager transactionManager;
	

	@Mock
	JobExecutionContext jobExecutionContext;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	JobExecutionException jobExecutionException;
	
	@Mock
	SyncTransaction syncTransaction;

	
	@InjectMocks
	JobProcessListener processListener;
	
	@Test
	public void toBeExecutedTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(syncTransaction);
		processListener.jobToBeExecuted(jobExecutionContext);

	}
	
	@Test
	public void toBeVetoedTest() {

		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(syncTransaction);processListener.jobExecutionVetoed(jobExecutionContext);

	}
	
	@Test
	public void wasExecutedTest() {

		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(syncTransaction);processListener.jobWasExecuted(jobExecutionContext, jobExecutionException);

	}
	
	@Test
	public void toBeExecutedExceptionTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		processListener.jobToBeExecuted(jobExecutionContext);

	}
	
	@Test
	public void toBeVetoedExceptionTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		processListener.jobExecutionVetoed(jobExecutionContext);

	}
	
	@Test
	public void wasExecutedExceptionTest() {
		Mockito.when(transactionManager.createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RegBaseUncheckedException.class);
		processListener.jobWasExecuted(jobExecutionContext, jobExecutionException);

	}
	

}
