/*package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.impl.LoginServiceImpl;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.dto.ResponseDTO;


public class IntegrationScenario02_PreregDataSyncPacketHandler extends BaseIntegrationTest {

	*//**
	 * Instance of LOGGER
	 *//*
	private static final Logger LOGGER = AppConfig.getLogger(IntegrationScenario02_PreregDataSyncPacketHandler.class);

	@Autowired
	private LoginService loginService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	GlobalParamService globalParamService;

	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;

	static String CENTERID = "10010";
	static String STATIONID = "10010";

	@Before
	public void setup() {

		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();

//		Map<String, Object> applicationMap = new HashMap<>();
//		applicationMap.put(RegistrationConstants.OTP_CHANNELS, "EMAIL");
//		applicationContext.setApplicationMap(applicationMap);

		Map<String, Object> map = globalParamService.getGlobalParams();
		map.put(RegistrationConstants.OTP_CHANNELS, "EMAIL");
		map.put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS, "120");
		SessionContext.userContext().setRegistrationCenterDetailDTO(
				loginService.getRegistrationCenterDetails(IntegrationTestConstants.RegistrationCenterId_val, "eng"));
		applicationContext.setApplicationMap(map);

		//ApplicationContext.map().put(RegistrationConstants.USER_CENTER_ID, CENTERID);
		//ApplicationContext.map().put(RegistrationConstants.USER_STATION_ID, STATIONID);

	}

	@Test
	public void testPreRegDataSyncIntegration() throws JsonProcessingException {
		// Validate if user is onboard
		//String userId = "110017";
		//List<String> list = commonUtil.validateUserId(userId);
//		if(list.contains("PWD")) {
//			
//		}

		//System.out.println(list.toString());

		//commonUtil.validateModeOfLogin(list, userId, "", "", "", "", "");
//		boolean isUserValid = commonUtil.verifyPassword(list, userId, "", "", "", "", "");
//        System.out.println(isUserValid);
		
		ResponseDTO responseDTO = preRegistrationDataSyncService
				.getPreRegistrationIds(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("line 84"); // TODO
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		assertNotNull(responseDTO.getSuccessResponseDTO());
		String preRegId = DBUtil.getPreRegIdFromDB();
		System.out.println("preRegId:: " + preRegId);	
	
	}

	@Test
	public void testPreRegDataSyncOtpLogin() {
		// String userId = "mosip";
		// 110004
		String userId = "110004";
		commonUtil.performOTPGenerationValidation(userId);
	}

}
*/