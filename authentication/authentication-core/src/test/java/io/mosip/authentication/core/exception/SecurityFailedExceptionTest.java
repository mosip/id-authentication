package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

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
	@Test
	public void SecurityFailedExceptionEnum() throws SecurityFailedException {
		assertThrows(SecurityFailedException.class, () -> {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}
	
	/**
	 * Security failed exception enum throwable.
	 *
	 * @throws SecurityFailedException the security failed exception
	 */
	@Test
	public void SecurityFailedExceptionEnumThrowable() throws SecurityFailedException {
		assertThrows(SecurityFailedException.class, () -> {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
			});
	}
	
	/**
	 * Security failed exception def cons.
	 *
	 * @throws SecurityFailedException the security failed exception
	 */
	@Test
	public void SecurityFailedExceptionDefCons() throws SecurityFailedException {
		assertThrows(SecurityFailedException.class, () -> {
		throw new SecurityFailedException();
			});
	}

}
