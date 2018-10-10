package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.AuthenticationFailedException;

public class AuthenticationFailedExceptionTest {
	
	@Test(expected=AuthenticationFailedException.class)
	public void testAuthenticationFailedExceptionDefaultCons() throws AuthenticationFailedException {
		throw new AuthenticationFailedException();
	}

	@Test(expected=AuthenticationFailedException.class)
	public void testAuthenticationFailedException() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=AuthenticationFailedException.class)
	public void testAuthenticationFailedExceptionThrowable() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
