package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertEquals;

import java.net.SocketTimeoutException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;

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
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		OtpValidatorResponseDTO otpValidatorResponseDTO = new OtpValidatorResponseDTO();
		otpValidatorResponseDTO.setstatus("success");
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap()))
				.thenReturn(otpValidatorResponseDTO);
		assertEquals(true, otpValidator.validate(authenticationValidatorDTO));
		
	}
}
