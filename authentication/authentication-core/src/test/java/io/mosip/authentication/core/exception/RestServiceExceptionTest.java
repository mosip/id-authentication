package io.mosip.authentication.core.exception;

import java.util.Optional;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
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
		throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, new RestServiceException());
	}
	
//	@Test(expected=RestServiceException.class)
//	public void testRestServiceExceptionObject() throws RestServiceException {
//		throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, Optional.of(new AuthRequestDTO()));
//	}

}
