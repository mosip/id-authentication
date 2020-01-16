package io.mosip.authentication.core.exception;

import java.io.IOException;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class IdAuthenticationBaseExceptionTest.
 *
 * @author Manoj SP
 */
public class IdAuthUncheckedExceptionTest {

	@Test(expected = IdAuthUncheckedException.class)
	public void IdAuthUncheckedExceptionTest1() throws IdAuthenticationBaseException {
		throw new IdAuthUncheckedException();
	}
	
	@Test(expected = IdAuthUncheckedException.class)
	public void IdAuthUncheckedExceptionTest2() throws IdAuthenticationBaseException {
		throw new IdAuthUncheckedException("abcd", "message");
	}
	
	@Test(expected = IdAuthUncheckedException.class)
	public void IdAuthUncheckedExceptionTest3() throws IdAuthenticationBaseException {
		throw new IdAuthUncheckedException("abcd", "message", new IOException());
	}
	
	@Test(expected = IdAuthUncheckedException.class)
	public void IdAuthUncheckedExceptionTest4() throws IdAuthenticationBaseException {
		throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR);
	}
	
	@Test(expected = IdAuthUncheckedException.class)
	public void IdAuthUncheckedExceptionTest5() throws IdAuthenticationBaseException {
		throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR, new IOException());
	}

}
