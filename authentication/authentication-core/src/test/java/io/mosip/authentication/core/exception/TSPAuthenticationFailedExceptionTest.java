package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.TSPAuthenticationFailedException;

/**
 * The Class TSPAuthenticationFailedExceptionTest.
 *
 * @author Manoj SP
 */
public class TSPAuthenticationFailedExceptionTest {

	/**
	 * TSP authentication failed exception def con.
	 *
	 * @throws TSPAuthenticationFailedException the TSP authentication failed exception
	 */
	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionDefCon() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException();
	}
	
	/**
	 * TSP authentication failed exception enum.
	 *
	 * @throws TSPAuthenticationFailedException the TSP authentication failed exception
	 */
	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionEnum() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}
	
	/**
	 * TSP authentication failed exception enum throwable.
	 *
	 * @throws TSPAuthenticationFailedException the TSP authentication failed exception
	 */
	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionEnumThrowable() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}

}
