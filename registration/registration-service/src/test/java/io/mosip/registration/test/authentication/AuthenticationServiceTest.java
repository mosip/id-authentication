package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.validator.FingerprintValidatorImpl;
import io.mosip.registration.validator.OTPValidatorImpl;

public class AuthenticationServiceTest {

	@InjectMocks
	private AuthenticationService authenticationService;
	
	@Mock
	FingerprintValidatorImpl fingerprintValidator;

	@Mock
	OTPValidatorImpl otpValidator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void getOtpValidatorTest() {
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		assertTrue(authenticationService.authValidator("otp", authenticationValidatorDTO));
	}
	
	@Test
	public void getFPValidatorTest() {
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		assertTrue(authenticationService.authValidator("Fingerprint", authenticationValidatorDTO));
	}

}
