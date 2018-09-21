package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class AuthenticationFailedExceptionTest {

	@Test(expected=AuthenticationFailedException.class)
	public void test3() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=AuthenticationFailedException.class)
	public void test4() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
