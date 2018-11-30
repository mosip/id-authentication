package io.mosip.registration.test.jobs;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.dao.impl.JobTransactionDAOImpl;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.SyncTransactionRepository;

public class JobTransactionDAOTest {
	@Mock
   private Logger logger;
	
	@Mock
	private SyncTransactionRepository syncTranscRepository; 

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private JobTransactionDAOImpl jobTransactionDAOImpl;

	@Test
	public void saveTest()  {
		SyncTransaction syncTransaction=new SyncTransaction();
		Mockito.when(syncTranscRepository.save(Mockito.any())).thenReturn(new SyncTransaction());
		jobTransactionDAOImpl.save(syncTransaction);
	}

	
}
