package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

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
	@Test
	public void testThrowUnknownException() throws IDAuthenticationUnknownException {
		assertThrows(IDAuthenticationUnknownException.class, () -> {
		throw new IDAuthenticationUnknownException();
			});
	}
	
	/**
	 * Test throw unknown exception with parameter.
	 *
	 * @throws IDAuthenticationUnknownException the ID authentication unknown exception
	 */
	@Test
	public void testThrowUnknownExceptionWithParameter() throws IDAuthenticationUnknownException {
		assertThrows(IDAuthenticationUnknownException.class, () -> {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
			});
	}
	
	/**
	 * Test throw unknown exception with parameter throwable.
	 *
	 * @throws IDAuthenticationUnknownException the ID authentication unknown exception
	 */
	@Test
	public void testThrowUnknownExceptionWithParameterThrowable() throws IDAuthenticationUnknownException {
		assertThrows(IDAuthenticationUnknownException.class, () -> {
		throw new IDAuthenticationUnknownException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, null);
			});
	}

}
