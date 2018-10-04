package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.RestServiceException;

public class RestServiceExceptionTest {
	
	@Test(expected=RestServiceException.class)
	public void testRestServiceExceptionDefaultCons() throws RestServiceException {
		throw new RestServiceException();
	}

	@Test(expected=RestServiceException.class)
	public void testRestServiceException() throws RestServiceException {
		throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=RestServiceException.class)
	public void testRestServiceExceptionThrowable() throws RestServiceException {
		throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
