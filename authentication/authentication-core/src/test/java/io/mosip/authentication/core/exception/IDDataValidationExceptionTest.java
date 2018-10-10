package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;

public class IDDataValidationExceptionTest {
	
	@Test(expected=IDDataValidationException.class)
	public void testIDDataValidationExceptionDefaultCons() throws IDDataValidationException {
		throw new IDDataValidationException();
	}

	@Test(expected=IDDataValidationException.class)
	public void testIDDataValidationException() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=IDDataValidationException.class)
	public void testIDDataValidationExceptionThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
