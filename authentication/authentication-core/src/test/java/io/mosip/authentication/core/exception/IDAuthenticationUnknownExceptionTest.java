package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDAuthenticationUnknownException;

public class IDAuthenticationUnknownExceptionTest {

	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownException() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException();
	}
	
	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownExceptionWithParameter() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
	}
	
	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownExceptionWithParameterThrowable() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, null);
	}

}
