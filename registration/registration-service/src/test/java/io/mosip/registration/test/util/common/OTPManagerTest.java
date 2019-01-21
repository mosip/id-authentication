package io.mosip.registration.test.util.common;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.OtpValidatorResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;
import io.mosip.registration.validator.OTPValidatorImpl;



public class OTPManagerTest {

	@InjectMocks
	OTPManager otpManager;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	OTPValidatorImpl otpValidator;

	@Mock
	AuthenticationService authenticationService;

	@Test
	public void getOTPSuccessResponseTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("mosip");

		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put("otp", "09876");

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDTO.class)))
				.thenReturn(responseMap);
		assertNotNull(otpManager.getOTP(otpGeneratorRequestDTO.getKey()).getSuccessResponseDTO());
	}

	@Test
	public void getOTPFailureResponseTest()
			throws RegBaseCheckedException, HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("mo");
		HashMap<String, String> responseMap = new HashMap<>();

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDTO.class)))
				.thenReturn(responseMap);
		assertNotNull(otpManager.getOTP(otpGeneratorRequestDTO.getKey()).getErrorResponseDTOs());
	}

	@Test
	public void validateOTPSuccessTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		
		OtpValidatorResponseDTO otpValidatorResponseDTO=new OtpValidatorResponseDTO();
		
		otpValidatorResponseDTO.setstatus("success");
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(otpValidatorResponseDTO);
		
		assertNotNull(otpManager.validateOTP("mosip", "12345").getSuccessResponseDTO());
	}
	
	@Test
	public void validateOTPFailureTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		
		OtpValidatorResponseDTO otpValidatorResponseDTO=new OtpValidatorResponseDTO();
		
		otpValidatorResponseDTO.setstatus("failure");
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(otpValidatorResponseDTO);
		
		assertNotNull(otpManager.validateOTP("mosip", "12345").getErrorResponseDTOs());
	}
	
	@Test
	public void validateOTPExceptionTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean())).thenThrow(RegBaseCheckedException.class);
		
		assertNotNull(otpManager.validateOTP("mosip", "12345").getErrorResponseDTOs());
	}

	@Test
	public void getOTPFailureHTTPTest()
			throws RegBaseCheckedException, HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("mo");
		HashMap<String, String> responseMap = new HashMap<>();

		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDTO.class)))
				.thenThrow(HttpClientErrorException.class);

		otpManager.getOTP(otpGeneratorRequestDTO.getKey());

		
	} 
	
	@Test
	public void getOTPFailureIllegalTest()
			throws RegBaseCheckedException, HttpClientErrorException, ResourceAccessException, SocketTimeoutException {
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("mo");
		HashMap<String, String> responseMap = new HashMap<>();

		
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDTO.class)))
				.thenThrow(IllegalStateException.class);
		
		otpManager.getOTP(otpGeneratorRequestDTO.getKey());

	}

}
