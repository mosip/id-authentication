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

import io.mosip.registration.jobs.JobProcessListener;
import io.mosip.registration.manager.BaseTransactionManager;
import static org.mockito.Mockito.doNothing;

public class JobProcessorListenerTest {
	
	@Mock
	BaseTransactionManager transactionManager;
	

	@Mock
	JobExecutionContext jobExecutionContext;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	JobExecutionException jobExecutionException;

	
	@InjectMocks
	JobProcessListener processListener;
	
	@Test
	public void toBeExecutedTest() {
		doNothing().when(transactionManager).createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		processListener.jobToBeExecuted(jobExecutionContext);

	}
	
	@Test
	public void toBeVetoedTest() {
		doNothing().when(transactionManager).createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		processListener.jobExecutionVetoed(jobExecutionContext);

	}
	
	@Test
	public void wasExecutedTest() {
		doNothing().when(transactionManager).createSyncTransaction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		processListener.jobWasExecuted(jobExecutionContext, jobExecutionException);

	}
	

}
