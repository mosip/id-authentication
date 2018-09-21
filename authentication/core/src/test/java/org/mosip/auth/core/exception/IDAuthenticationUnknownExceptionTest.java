package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class IDAuthenticationUnknownExceptionTest {

	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownException() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException();
	}
	
	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownExceptionWithParameter() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
	}

}
