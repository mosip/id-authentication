package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.validator.AuthenticationBaseValidator;
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
		List<AuthenticationBaseValidator> authenticationBaseValidators=new ArrayList<>();
		authenticationBaseValidators.add(otpValidator);
		authenticationService.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		when(otpValidator.validate(authenticationValidatorDTO)).thenReturn(true);
		assertTrue(authenticationService.authValidator("otp", authenticationValidatorDTO));
	}
	
	@Test
	public void getFPValidatorTest() {
		List<AuthenticationBaseValidator> authenticationBaseValidators=new ArrayList<>();
		authenticationBaseValidators.add(fingerprintValidator);
		authenticationService.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		when(fingerprintValidator.validate(authenticationValidatorDTO)).thenReturn(true);
		assertTrue(authenticationService.authValidator("Fingerprint", authenticationValidatorDTO));
	}
	
	@Test
	public void getFPValidatorNegativeTest() {
		List<AuthenticationBaseValidator> authenticationBaseValidators=new ArrayList<>();
		authenticationBaseValidators.add(fingerprintValidator);
		authenticationService.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		when(fingerprintValidator.validate(authenticationValidatorDTO)).thenReturn(false);
		assertFalse(authenticationService.authValidator("otp", authenticationValidatorDTO));
	}

}
