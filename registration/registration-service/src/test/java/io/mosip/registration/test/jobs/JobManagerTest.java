package io.mosip.registration.test.jobs;

import java.util.HashMap;
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
import org.quartz.JobKey;
import org.quartz.Trigger;

import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.manager.impl.JobManagerImpl;

public class JobManagerTest {

	@Mock
	private JobExecutionContext jobExecutionContext;

	@Mock
	private JobDetail jobDetail;

	@Mock
	private Trigger trigger;
	@Mock
	private JobDataMap jobDataMap;
	
	@InjectMocks
	JobManagerImpl jobManagerImpl;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	
	@Before
	public void initiate() {
		
		JobKey jobKey=new JobKey("1");
		
						
		Mockito.when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
		Mockito.when(jobDetail.getKey()).thenReturn(jobKey);
		Mockito.when(trigger.getJobDataMap()).thenReturn(jobDataMap);
		Mockito.when(jobDataMap.get(Mockito.any())).thenReturn(jobDetail);
	}
	
	@Test
	public void getJobIDTest() {
		Assert.assertSame(jobManagerImpl.getJobId(jobExecutionContext), "1");
		Assert.assertSame(jobManagerImpl.getJobId(jobDetail), "1");
		Assert.assertSame(jobManagerImpl.getJobId(trigger), "1");

	}
	
	@Test
	public void getChildJobsTest() {
		SyncJobDef syncJobDef=new SyncJobDef();
		syncJobDef.setId("1");
		Map<String, SyncJobDef> jobMap=new HashMap<>();
		jobMap.put(syncJobDef.getId(), syncJobDef);
		
		JobDataMap jobDataMap1=new JobDataMap(jobMap);

		Mockito.when(jobDetail.getJobDataMap()).thenReturn(jobDataMap1);
		
		jobManagerImpl.getChildJobs(jobExecutionContext);
	}

}
