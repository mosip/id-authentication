package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

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
