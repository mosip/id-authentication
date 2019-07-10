package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.validator.OTPValidatorImpl;

public class OTPValidatorTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private OTPManager otpManager;

	@InjectMocks
	private OTPValidatorImpl otpValidator;

	@Test
	
	public void OtpValidateTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		assertEquals(false, otpValidator.validate(authenticationValidatorDTO));

	}

	@Test
	public void OtpValidateAuthTest()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		AuthTokenDTO authTokenDTO = new AuthTokenDTO();
		when(otpManager.validateOTP(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(authTokenDTO);
		assertEquals(authTokenDTO, otpValidator.validate("mosip","12345", true));

	}

/*	@SuppressWarnings("unchecked")
	@Test
	public void testCheckedException() throws URISyntaxException, RegBaseCheckedException, HttpClientErrorException,
			HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap()))
				.thenThrow(new HttpClientErrorException(HttpStatus.ACCEPTED));
		assertEquals(false, otpValidator.validate(authenticationValidatorDTO));
	}*/
}
