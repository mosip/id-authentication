package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.ServiceTimeoutException;

public class ServiceTimeoutExceptionTest {
	
	@Test(expected=ServiceTimeoutException.class)
	public void ServiceTimeoutExceptionDefCon() throws ServiceTimeoutException {
		throw new ServiceTimeoutException();
	}

	@Test(expected=ServiceTimeoutException.class)
	public void ServiceTimeoutExceptionEnum() throws ServiceTimeoutException {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=ServiceTimeoutException.class)
	public void ServiceTimeoutExceptionEnumThrowable() throws ServiceTimeoutException {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
