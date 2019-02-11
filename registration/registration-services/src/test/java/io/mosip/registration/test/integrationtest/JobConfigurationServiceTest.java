package io.mosip.registration.test.integrationtest;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.castor.util.concurrent.Sync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.repositories.JobConfigRepository;
import io.mosip.registration.service.config.JobConfigurationService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class, DaoConfig.class })
public class JobConfigurationServiceTest {

	@Autowired
	JobConfigurationService jobConfigurationService;
	@Autowired
	private JobConfigRepository jobConfigRepository;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
	}

	/**
	 * creates a job config * Active Jobs Map *
	 */
	@Test
	public void initiateJobsTest() {
		jobConfigurationService.initiateJobs();
	}

	/***
	 * Starts Scheduler
	 */
	@Test
	public void startSchedulerTest() {
		ResponseDTO response = jobConfigurationService.startScheduler(Initialization.getApplicationContext());
		assertEquals(response.getErrorResponseDTOs(), null);
		assertEquals(response.getSuccessResponseDTO(), null);
	}

	@Test
	public void getCurrentRunningJobDetailsTest() {
		/**
		 * Create Copy of data
		 */
		List<SyncJobDef> data = jobConfigRepository.findAll();
		if (data.size() > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("s1", false);
			params.put("s2", true);
			jobConfigRepository.createQueryUpdateOrDelete("UPDATE SyncJobDef  SET IS_ACTIVE=:s1 where IS_ACTIVE=:s2",
					params);
			ResponseDTO response = jobConfigurationService.getCurrentRunningJobDetails();
			assertEquals(response.getErrorResponseDTOs().get(0).getCode(), RegistrationConstants.ERROR);
			assertEquals(response.getErrorResponseDTOs().get(0).getMessage(), RegistrationConstants.NO_JOBS_RUNNING);
			for (SyncJobDef def : data) {
				jobConfigRepository.save(def);
			}
			response = jobConfigurationService.getCurrentRunningJobDetails();
		} else {
			System.out.println("Nothing is there no Testing can be done");
		}

	}

}
