package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertEquals;

import java.net.SocketTimeoutException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.OtpValidatorResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;
import io.mosip.registration.validator.OTPValidator;

public class OTPValidatorTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@InjectMocks
	private OTPValidator otpValidator;

	@SuppressWarnings("unchecked")
	@Test
	public void OtpValidateTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		OtpValidatorResponseDTO otpValidatorResponseDTO = new OtpValidatorResponseDTO();
		otpValidatorResponseDTO.setstatus("success");
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap()))
				.thenReturn(otpValidatorResponseDTO);
		assertEquals(true, otpValidator.validate(authenticationValidatorDTO));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void OtpValidateMismatchTest()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		OtpValidatorResponseDTO otpValidatorResponseDTO = new OtpValidatorResponseDTO();
		otpValidatorResponseDTO.setstatus("failure");
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap()))
				.thenReturn(otpValidatorResponseDTO);
		assertEquals(false, otpValidator.validate(authenticationValidatorDTO));

	}

	@SuppressWarnings("unchecked")
	public void testCheckedException() throws URISyntaxException, RegBaseCheckedException, HttpClientErrorException,
			HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		assertEquals(false, otpValidator.validate(authenticationValidatorDTO));
	}
}
