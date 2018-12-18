package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.validator.AuthenticationService;
import io.mosip.registration.validator.FingerprintValidator;
import io.mosip.registration.validator.OTPValidator;

public class AuthenticationServiceTest {

	@InjectMocks
	private AuthenticationService authenticationService;
	
	@Mock
	FingerprintValidator fingerprintValidator;

	@Mock
	OTPValidator otpValidator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void getOtpValidatorTest() {
		assertThat(authenticationService.getValidator("otp"), is(otpValidator));
	}
	
	@Test
	public void getFPValidatorTest() {
		assertThat(authenticationService.getValidator("Fingerprint"), is(fingerprintValidator));
	}

}
