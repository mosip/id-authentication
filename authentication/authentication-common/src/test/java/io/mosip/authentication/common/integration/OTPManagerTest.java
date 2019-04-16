package io.mosip.authentication.common.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.factory.AuditRequestFactory;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.common.service.integration.dto.OTPValidateResponseDTO;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.common.service.integration.dto.OtpValidateRequestDTO;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.kernel.core.http.ResponseWrapper;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OTPManagerTest.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPManagerTest {

	@Mock
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	AuditRequestFactory auditFactory;

	private OtpGeneratorRequestDto otpGeneratorRequestDto;

	@Autowired
	Environment environment;

	@Mock
	private RestHelper restHelper;

	@Mock
	RestServiceException e;

	@InjectMocks
	private OTPManager otpManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(auditFactory, "env", environment);

	}

	private static final String VALIDATION_UNSUCCESSFUL = "VALIDATION_UNSUCCESSFUL";

	private static final String OTP_EXPIRED = "OTP_EXPIRED";

	private static final String USER_BLOCKED = "USER_BLOCKED";

	@SuppressWarnings("rawtypes")
	@Test
	public void otpTest() throws RestServiceException, IdAuthenticationBusinessException {
		String otpKey = "12345";
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(otpKey);

		OtpGeneratorResponseDto otpGeneratorResponsetDto = new OtpGeneratorResponseDto("870698", "success", "valid");
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		ResponseWrapper<Map> response1 = new ResponseWrapper<>();
		Map<String, Object> map = new HashMap<>();
		map.put("otp", "870698");
		map.put("status", "success");
		map.put("messaage", "valid");
		response1.setResponse(map);

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);

		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response1);
		String response = otpGeneratorResponsetDto.getOtp();
		String expactedOTP = otpManager.generateOTP(otpKey);
		assertEquals(response, expactedOTP);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestGenerateKeyForBlockedUser() throws RestServiceException, IdAuthenticationBusinessException {

		OtpGeneratorResponseDto otpGeneratorResponsetDto = new OtpGeneratorResponseDto();
		otpGeneratorResponsetDto.setStatus("failure");
		otpGeneratorResponsetDto.setMessage("USER_BLOCKED");

		Map<String, String> valueMap = new HashMap();
		valueMap.put("status", "failure");
		valueMap.put("message", "USER_BLOCKED");
		Map<String, Object> wrapperMap = new HashMap();
		wrapperMap.put("response", valueMap);
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE, wrapperMap.toString(), wrapperMap));
		otpManager.generateOTP("123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceException()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("OTP_EXPIRED");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valuemap = new HashMap<>();
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(
				new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, valuemap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test
	public void TestOtpAuth() throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "VALIDATION_SUCCESSFUL");
		Map<String, Object> response1 = new HashMap<>();
		response1.put("response", response);
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response1);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
		assertTrue(expactedOTP);
	}

	@Test
	public void TestOtpAuthFailure()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", "Validation_Unsuccessful");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(valueMap);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
		assertFalse(expactedOTP);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalid()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("VALIDATION_UNSUCCESSFUL");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "VALIDATION_UNSUCCESSFUL");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(
				new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, response));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidUnknownMessage()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("SOME UNKNOWN MESSAGE");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "VALIDATION_UNSUCCESSFUL");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(
				new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, response));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatus()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "VALIDATION_UNSUCCESSFUL");
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(
				new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, response));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatusWithOTPNOTGENERATEDError()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		Map<String, Object> valuemap = new HashMap<>();
		valuemap.put("status", "failure");
		String output = "{\"errors\":[{\"errorCode\":\"KER-OTV-005\",\"errorMessage\":\"Validation can't be performed against this key. Generate OTP first.\"}]}";
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		RestServiceException restServiceException = new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, valuemap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatusWithSOMEOTHERERRORDError()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failure");
		response.put("message", "VALIDATION_UNSUCCESSFUL");
		otpGeneratorRequestDto.setKey("Invalid");
		String output = "{\"errors\":[{\"errorCode\":\"KER-SOME-OTHER-001\",\"errorMessage\":\"Some other error.\"}]}";
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		RestServiceException restServiceException = new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, response);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestDataValidationException() throws IdAuthenticationBusinessException {
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenThrow(new IDDataValidationException());
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testValidateOTP_ThrowRestServiceExceptionWith_StatusFailureAndMessageUSER_BLOCKED()
			throws JsonProcessingException, RestServiceException, IdAuthenticationBusinessException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("USER_BLOCKED");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<Object, Object> valueMap = new HashMap<>();
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(
				new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestResponseBodyisEmpty() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.INVALID_REST_SERVICE));
		otpManager.generateOTP("123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowOtpException_UINLocked() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", USER_BLOCKED);

		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, valueMap.toString(), (Object) valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowOtpException_OtpExpired() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", OTP_EXPIRED);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, valueMap.toString(), (Object) valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowOtpException_ValidationUnsuccessful()
			throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", VALIDATION_UNSUCCESSFUL);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, valueMap.toString(), (Object) valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowOtpException_ValidationUnsuccessful_Invalid()
			throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", "invalid");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, valueMap.toString(), (Object) valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestthrowOTPException() throws Throwable {
		try {
			ReflectionTestUtils.invokeMethod(otpManager, "throwOtpException", OTP_EXPIRED);
		} catch (Exception e) {
			throw e.getCause();
		}

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestThrowKeynotFound() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", 12343);
		valueMap.put("message", 12343);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, valueMap.toString(), (Object) valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestthrowKeyNotFoundException() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "failure");
		valueMap.put("message", OTP_EXPIRED);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, valueMap.toString(), (Object) valueMap));
		otpManager.validateOtp("Test123", "123456");
	}

	// ====================================================================
	// ********************** Helper Method *******************************
	// ====================================================================
	private RestRequestDTO getRestRequestDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		restRequestDTO.setRequestBody(otpGeneratorRequestDto);
		restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

	private RestRequestDTO getRestRequestvalidDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		OtpValidateRequestDTO OtpValidateRequestDTO = new OtpValidateRequestDTO();
		OtpValidateRequestDTO.setKey("Test");
		OtpValidateRequestDTO.setKey("123456");
		restRequestDTO.setRequestBody(OtpValidateRequestDTO);
		restRequestDTO.setResponseType(Map.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

}
