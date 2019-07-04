package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.integration.dto.OTPValidateResponseDTO;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.PinDTO;

/**
 * 
 * @author Dinesh Karuppiah
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidateOtpRequestTest {

	@InjectMocks
	RestHelperImpl restHelper;

	@Autowired
	MockMvc mvc;

	@Autowired
	Environment env;

	@Mock
	OTPValidateResponseDTO otpvalidateresponsedto;

	@InjectMocks
	RestRequestFactory restfactory;

	@InjectMocks
	OTPManager otpManager;

	@InjectMocks
	PinDTO pindto;

	@Before
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
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class))).thenReturn(valuemap);
		RestRequestDTO requestDTO = new RestRequestDTO();
		RestRequestFactory restreqfactory = Mockito.mock(RestRequestFactory.class);
		Mockito.when(
				restreqfactory.buildRequest(Mockito.any(RestServicesConstants.class), Mockito.any(), Mockito.any()))
				.thenReturn(requestDTO);
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restreqfactory);
//		ReflectionTestUtils.setField(otpManager, "otpvalidateresponsedto", otpvalidateresponsedto);

		// TODO: for validate OTP as true
		assertEquals(false, otpManager.validateOtp("12345", "23232"));
	}

	@Test
	public void zTest_InvalidvalidateOTP() throws RestServiceException, IdAuthenticationBusinessException {
		RestHelper helper = Mockito.mock(RestHelper.class);
		Map<String,Object> valuemap=new HashMap<>();
		valuemap.put("status", "failure");
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class))).thenReturn(valuemap);
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);

		assertFalse(otpManager.validateOtp("2323", "2323"));
	}

}