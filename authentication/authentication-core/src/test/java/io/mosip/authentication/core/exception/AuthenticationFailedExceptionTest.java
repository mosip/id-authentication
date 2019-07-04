package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.AuthenticationFailedException;

/**
 * The Class AuthenticationFailedExceptionTest.
 *
 * @author Manoj SP
 */
public class AuthenticationFailedExceptionTest {

	/**
	 * Test authentication failed exception default cons.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test(expected = AuthenticationFailedException.class)
	public void testAuthenticationFailedExceptionDefaultCons() throws AuthenticationFailedException {
		throw new AuthenticationFailedException();
	}

	/**
	 * Test authentication failed exception.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test(expected = AuthenticationFailedException.class)
	public void testAuthenticationFailedException() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}

	/**
	 * Test authentication failed exception throwable.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test(expected = AuthenticationFailedException.class)
	public void testAuthenticationFailedExceptionThrowable() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}

	/**
	 * Test authenticationexception constant.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test(expected = AuthenticationFailedException.class)
	public void TestAuthenticationexceptionConstant() throws AuthenticationFailedException {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}

}
