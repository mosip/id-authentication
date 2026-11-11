package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

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
	@Test
	public void ServiceTimeoutExceptionDefCon() throws ServiceTimeoutException {
		assertThrows(ServiceTimeoutException.class, () -> {
		throw new ServiceTimeoutException();
			});
	}

	/**
	 * Service timeout exception enum.
	 *
	 * @throws ServiceTimeoutException the service timeout exception
	 */
	@Test
	public void ServiceTimeoutExceptionEnum() throws ServiceTimeoutException {
		assertThrows(ServiceTimeoutException.class, () -> {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}
	
	/**
	 * Service timeout exception enum throwable.
	 *
	 * @throws ServiceTimeoutException the service timeout exception
	 */
	@Test
	public void ServiceTimeoutExceptionEnumThrowable() throws ServiceTimeoutException {
		assertThrows(ServiceTimeoutException.class, () -> {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
			});
	}

}
