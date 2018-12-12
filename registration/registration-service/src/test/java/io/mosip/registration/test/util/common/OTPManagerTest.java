package io.mosip.registration.test.util.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;
import io.mosip.registration.validator.AuthenticationService;
import io.mosip.registration.validator.OTPValidator;

public class OTPManagerTest {

	@InjectMocks
	OTPManager otpManager;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	OTPValidator otpValidator;

	@Mock
	AuthenticationService authenticationService;

	private ApplicationContext applicationContext = ApplicationContext.getInstance();

	@Before
	public void initialize() throws IOException, URISyntaxException {
		applicationContext.setApplicationMessagesBundle();
	}

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
	public void validateOTPSuccessTest() {
		when(authenticationService.getValidator("otp")).thenReturn(otpValidator);
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setOtp("12345");
		authenticationValidatorDTO.setUserId("mosip");
		when(otpValidator.validate(authenticationValidatorDTO)).thenReturn(true);
		assertThat(otpManager.validateOTP("mosip", "12345"), is(false));
	}

}
