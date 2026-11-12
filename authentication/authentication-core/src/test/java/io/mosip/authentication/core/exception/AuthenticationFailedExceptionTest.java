package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

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
	@Test
	public void testAuthenticationFailedExceptionDefaultCons() throws AuthenticationFailedException {
		assertThrows(AuthenticationFailedException.class, () -> {
		throw new AuthenticationFailedException();
			});
	}

	/**
	 * Test authentication failed exception.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test
	public void testAuthenticationFailedException() throws AuthenticationFailedException {
		assertThrows(AuthenticationFailedException.class, () -> {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
			});
	}

	/**
	 * Test authentication failed exception throwable.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test
	public void testAuthenticationFailedExceptionThrowable() throws AuthenticationFailedException {
		assertThrows(AuthenticationFailedException.class, () -> {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
			});
	}

	/**
	 * Test authenticationexception constant.
	 *
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	@Test
	public void TestAuthenticationexceptionConstant() throws AuthenticationFailedException {
		assertThrows(AuthenticationFailedException.class, () -> {
		throw new AuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}

}
