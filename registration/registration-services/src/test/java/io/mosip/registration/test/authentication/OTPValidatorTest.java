package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
<<<<<<< HEAD
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
=======
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
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
<<<<<<< HEAD
		ResponseDTO responseDTO=new ResponseDTO();
		SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
	AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		authenticationValidatorDTO.setOtp("1234");
		when(otpManager.validateOTP(authenticationValidatorDTO.getUserId(), authenticationValidatorDTO.getOtp())).thenReturn(responseDTO);
=======
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
		assertEquals(false, otpValidator.validate(authenticationValidatorDTO));

	}

	@Test
	public void OtpValidateAuthTest()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		AuthTokenDTO authTokenDTO = new AuthTokenDTO();
		when(otpManager.validateOTP("mosip","12345")).thenReturn(authTokenDTO);
		assertEquals(authTokenDTO, otpValidator.validate("mosip","12345"));

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
