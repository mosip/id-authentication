package io.mosip.registration.test.dao.impl;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.dao.impl.SyncJobConfigDAOImpl;
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
		private SyncJobConfigDAOImpl jobConfigDAOImpl;
		
		
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
		
		@Test
		public void updateAllJobsTest() {
			
			List<SyncJobDef> list=new LinkedList<>();
			SyncJobDef jobDef =new SyncJobDef();
			jobDef.setId("12345");
			list.add(jobDef);
			
			Iterable<SyncJobDef> iterable=list;
			
			Mockito.when(jobConfigRepository.saveAll(iterable)).thenReturn(list);
			assertEquals(jobConfigDAOImpl.updateAll(list),list);
		}


}
