package io.mosip.registration.test.jobs;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.dao.impl.JobConfigDAOImpl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.JobConfigRepository;

public class JobConfigDAOTest {
	
	   @Mock
	   private Logger logger;
		
		@Mock
		private JobConfigRepository jobConfigRepository; 

		@Rule
		public MockitoRule mockitoRule = MockitoJUnit.rule();

		@InjectMocks
		private JobConfigDAOImpl jobConfigDAOImpl;
		
		
		@Test
		public void saveTest()  {			
			Mockito.when(jobConfigRepository.findAll()).thenReturn(new LinkedList<SyncJobDef>());
			jobConfigDAOImpl.getAll();
		}
		
		@Test(expected = RegBaseUncheckedException.class)
		public void saveExceptionTest()  {
			Mockito.when(jobConfigRepository.findAll()).thenThrow(RegBaseUncheckedException.class);
			jobConfigDAOImpl.getAll();
		}
		
		@Test
		public void getActiveJobsTest() {
			Mockito.when(jobConfigRepository.findByIsActiveTrue()).thenReturn(new LinkedList<SyncJobDef>());
			assertThat(jobConfigDAOImpl.getActiveJobs(), is(new LinkedList<SyncJobDef>()));
		}


}
