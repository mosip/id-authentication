package io.mosip.registration.test.integrationtest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.util.common.OTPManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OTPManagerTest extends BaseIntegrationTest {

	@Autowired
	GlobalParamService globalParamService;

	@Autowired
	OTPManager otpManager;
	
	@Autowired
	CommonUtil commonUtil;

	@BeforeClass
	public static void setup() {
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.OTP_CHANNELS, "EMAIL");
		ApplicationContext.getInstance().setApplicationMap(applicationMap);
	}

	@Test
	public void testGetOTP() {
		String userId = "110017";
		ResponseDTO response = otpManager.getOTP(userId);
		SuccessResponseDTO successResponse = response.getSuccessResponseDTO();
		List<ErrorResponseDTO> errorDtos = response.getErrorResponseDTOs();
		assertNotNull(successResponse.getMessage());
	}
	
	@Test
	public void testValidateOTP() {
		String userId = "110017";
		commonUtil.performOTPGenerationValidation(userId);
		
	}

}
