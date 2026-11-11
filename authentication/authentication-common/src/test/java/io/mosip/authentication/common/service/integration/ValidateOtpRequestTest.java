package io.mosip.authentication.common.service.integration;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.MethodOrderer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.OTPValidateResponseDTO;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.PinDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.authentication.common.service.helper.RestHelper;

/**
 * 
 * @author Dinesh Karuppiah
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled
public class ValidateOtpRequestTest {

	@InjectMocks
	RestHelper restHelper;

	@Autowired
	MockMvc mvc;

	@Autowired
	EnvUtil env;

	@Mock
	OTPValidateResponseDTO otpvalidateresponsedto;

	@InjectMocks
	RestRequestFactory restfactory;

	@InjectMocks
	OTPManager otpManager;

	@InjectMocks
	PinDTO pindto;
	
	@Autowired
	ObjectMapper mapper;

	@Autowired
	private ValidateOtpHelper validateOtpHelper;

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(restfactory, "env", env);
		ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restfactory);
	}

	/**
	 * Test OTP Validation with key and OTP on Core-kernal
	 * 
	 * @throws IdAuthenticationBusinessException
	 * @throws RestServiceException
	 */

	@Test
	public void Test() throws IdAuthenticationBusinessException, RestServiceException {
		Map<String,Object> valuemap=new HashMap<>();
		valuemap.put("status", "failure");
		RestHelper helper = Mockito.mock(RestHelper.class);
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class), Mockito.any())).thenReturn(valuemap);
		RestRequestDTO requestDTO = new RestRequestDTO();
		RestRequestFactory restreqfactory = Mockito.mock(RestRequestFactory.class);
		Mockito.when(
				restreqfactory.buildRequest(Mockito.any(RestServicesConstants.class), Mockito.any(), Mockito.any()))
				.thenReturn(requestDTO);
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restreqfactory);
//		ReflectionTestUtils.setField(otpManager, "otpvalidateresponsedto", otpvalidateresponsedto);

		// TODO: for validate OTP as true
		assertEquals(false, validateOtpHelper.validateOtp("12345", "23232", "426789089018"));
	}

	@Test
	public void zTest_InvalidvalidateOTP() throws RestServiceException, IdAuthenticationBusinessException {
		RestHelper helper = Mockito.mock(RestHelper.class);
		Map<String,Object> valuemap=new HashMap<>();
		valuemap.put("status", "failure");
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class), Mockito.any())).thenReturn(valuemap);
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);

		assertFalse(validateOtpHelper.validateOtp("2323", "2323", "426789089018"));
	}

}