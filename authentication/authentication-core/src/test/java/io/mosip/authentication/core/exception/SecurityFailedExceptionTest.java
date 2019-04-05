package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.SecurityFailedException;

/**
 * The Class SecurityFailedExceptionTest.
 *
 * @author Manoj SP
 */
public class SecurityFailedExceptionTest {

	/**
	 * Security failed exception enum.
	 *
	 * @throws SecurityFailedException the security failed exception
	 */
	@Test(expected=SecurityFailedException.class)
	public void SecurityFailedExceptionEnum() throws SecurityFailedException {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}
	
	/**
	 * Security failed exception enum throwable.
	 *
	 * @throws SecurityFailedException the security failed exception
	 */
	@Test(expected=SecurityFailedException.class)
	public void SecurityFailedExceptionEnumThrowable() throws SecurityFailedException {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}
	
	/**
	 * Security failed exception def cons.
	 *
	 * @throws SecurityFailedException the security failed exception
	 */
	@Test(expected=SecurityFailedException.class)
	public void SecurityFailedExceptionDefCons() throws SecurityFailedException {
		throw new SecurityFailedException();
	}

}
