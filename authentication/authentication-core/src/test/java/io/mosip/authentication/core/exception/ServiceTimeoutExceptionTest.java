package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.ServiceTimeoutException;

/**
 * The Class ServiceTimeoutExceptionTest.
 *
 * @author Manoj SP
 */
public class ServiceTimeoutExceptionTest {
	
	/**
	 * Service timeout exception def con.
	 *
	 * @throws ServiceTimeoutException the service timeout exception
	 */
	@Test(expected=ServiceTimeoutException.class)
	public void ServiceTimeoutExceptionDefCon() throws ServiceTimeoutException {
		throw new ServiceTimeoutException();
	}

	/**
	 * Service timeout exception enum.
	 *
	 * @throws ServiceTimeoutException the service timeout exception
	 */
	@Test(expected=ServiceTimeoutException.class)
	public void ServiceTimeoutExceptionEnum() throws ServiceTimeoutException {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}
	
	/**
	 * Service timeout exception enum throwable.
	 *
	 * @throws ServiceTimeoutException the service timeout exception
	 */
	@Test(expected=ServiceTimeoutException.class)
	public void ServiceTimeoutExceptionEnumThrowable() throws ServiceTimeoutException {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}

}
