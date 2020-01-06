package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDAuthenticationUnknownException;

/**
 * The Class IDAuthenticationUnknownExceptionTest.
 *
 * @author Manoj SP
 */
public class IDAuthenticationUnknownExceptionTest {

	/**
	 * Test throw unknown exception.
	 *
	 * @throws IDAuthenticationUnknownException the ID authentication unknown exception
	 */
	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownException() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException();
	}
	
	/**
	 * Test throw unknown exception with parameter.
	 *
	 * @throws IDAuthenticationUnknownException the ID authentication unknown exception
	 */
	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownExceptionWithParameter() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
	}
	
	/**
	 * Test throw unknown exception with parameter throwable.
	 *
	 * @throws IDAuthenticationUnknownException the ID authentication unknown exception
	 */
	@Test(expected=IDAuthenticationUnknownException.class)
	public void testThrowUnknownExceptionWithParameterThrowable() throws IDAuthenticationUnknownException {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, null);
	}

}
