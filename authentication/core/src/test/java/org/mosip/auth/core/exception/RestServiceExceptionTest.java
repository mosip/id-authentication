package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

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
