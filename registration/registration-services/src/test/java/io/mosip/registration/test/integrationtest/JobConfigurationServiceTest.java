package io.mosip.registration.test.integrationtest;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
////import static org.testng.Assert.assertTrue;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.RegistrationConstants;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.dto.ErrorResponseDTO;
//import io.mosip.registration.dto.ResponseDTO;
//import io.mosip.registration.dto.SuccessResponseDTO;
//import io.mosip.registration.dto.SyncDataProcessDTO;
//import io.mosip.registration.entity.SyncJobDef;
//import io.mosip.registration.repositories.JobConfigRepository;
//import io.mosip.registration.service.config.GlobalParamService;
//import io.mosip.registration.service.config.JobConfigurationService;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
public class JobConfigurationServiceTest extends BaseIntegrationTest {

//	@Autowired
//	JobConfigurationService jobConfigurationService;
//
//	@Autowired
//	private JobConfigRepository jobConfigRepository;
//
//	@Autowired
//	private GlobalParamService globalParamService;
//
//	// private static GlobalParamService globalParamService;
//
//	private static ApplicationContext applicationContext;
//
//	@BeforeClass
//	public static void setUp() {
//		applicationContext = ApplicationContext.getInstance();
//		applicationContext.setApplicationLanguageBundle();
//		applicationContext.setApplicationMessagesBundle();
//		applicationContext.setLocalLanguageProperty();
//		applicationContext.setLocalMessagesBundle();
//
//		// applicationContext.setApplicationMap(globalParamService.getGlobalParams());
//	}
//
//	/**
//	 * creates a job config * Active Jobs Map *
//	 */
//	@Test
//	public void initiateJobsTest() {
//		jobConfigurationService.initiateJobs();
//	}
//
//	/**
//	 * Starts Scheduler
//	 */
//	@Test
//	public void startSchedulerTest() {
//		ResponseDTO response = jobConfigurationService.startScheduler();
//		assertNotNull(response);
//		// code=ERROR, message=SYNC-DATA Process already running, otherAttributes=null,
//		// infoType=null
//		assertEquals("BATCH_JOB_START_SUCCESS_MESSAGE", response.getSuccessResponseDTO().getMessage());
//		assertNull(response.getErrorResponseDTOs());
////		assertEquals(response.getErrorResponseDTOs().get(0).getCode(), "ERROR");
////		assertEquals("SYNC_DATA_PROCESS_ALREADY_STARTED", response.getErrorResponseDTOs().get(0).getMessage());
//	}
//
//	/**
//	 * 
//	 */
//	@Test
//	public void getCurrentRunningJobDetailsTest() {
//		/**
//		 * Create Copy of data
//		 */
//		List<SyncJobDef> data = jobConfigRepository.findAll();
//		if (data.size() > 0) {
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("s1", false);
//			params.put("s2", true);
//			jobConfigRepository.createQueryUpdateOrDelete("UPDATE SyncJobDef  SET IS_ACTIVE=:s1 where IS_ACTIVE=:s2",
//					params);
//			ResponseDTO response = jobConfigurationService.getCurrentRunningJobDetails();
//			assertEquals(response.getErrorResponseDTOs().get(0).getCode(), RegistrationConstants.ERROR);
//			assertEquals(response.getErrorResponseDTOs().get(0).getMessage(), RegistrationConstants.NO_JOBS_RUNNING);
//			for (SyncJobDef def : data) {
//				jobConfigRepository.save(def);
//			}
//			response = jobConfigurationService.getCurrentRunningJobDetails();
//		} else {
//			System.out.println("Nothing is there no Testing can be done");
//		}
//
//	}
//
//	@Test
//	public void stopSchedulerTest() {
//
//		ResponseDTO stopSchedulerResponseDto = null;
//		ResponseDTO response = jobConfigurationService.startScheduler();
//		if ("SYNC_DATA_PROCESS_ALREADY_STARTED".equals(response.getErrorResponseDTOs().get(0).getMessage())) {
//			stopSchedulerResponseDto = jobConfigurationService.stopScheduler();
//		}
//		SuccessResponseDTO successResponse = stopSchedulerResponseDto.getSuccessResponseDTO();
//		String code = successResponse.getCode();
//		String message = successResponse.getMessage();
//		System.out.println(code + ", message: " + message);
//		assertEquals("BATCH_JOB_STOP_SUCCESS_MESSAGE", message);
//	}
//
//	@Test
//	public void getLastCompletedSyncJobsTest() {
//
//		jobConfigurationService.initiateJobs();
//		jobConfigurationService.executeAllJobs();
//
//		ResponseDTO respDto = jobConfigurationService.getLastCompletedSyncJobs();
//		SuccessResponseDTO successResponse = respDto.getSuccessResponseDTO();
//		List<ErrorResponseDTO> errors = respDto.getErrorResponseDTOs();
//		System.out.println(successResponse.getMessage());
//		Map<String, Object> jobs = successResponse.getOtherAttributes();
//		assertTrue(jobs.size() > 0);
//
//	}
//
//	@Test
//	public void getSyncJobsTransactionTest() {
//
//		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
//
//		jobConfigurationService.initiateJobs();
//		jobConfigurationService.executeAllJobs();
//
//		ResponseDTO respDto = jobConfigurationService.getSyncJobsTransaction();
//		SuccessResponseDTO successRespDto = respDto.getSuccessResponseDTO();
//		List<ErrorResponseDTO> errors = respDto.getErrorResponseDTOs();
//		assertTrue(successRespDto.getOtherAttributes().size() > 0);
//		System.out.println(successRespDto.getMessage());
//		System.out.println(errors.toString());
//
//	}

}
