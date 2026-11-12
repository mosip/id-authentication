package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class IdAuthenticationBaseExceptionTest.
 *
 * @author Manoj SP
 */
public class IdAuthUncheckedExceptionTest {

	@Test
	public void IdAuthUncheckedExceptionTest1() throws IdAuthenticationBaseException {
		assertThrows(IdAuthUncheckedException.class, () -> {
		throw new IdAuthUncheckedException();
			});
	}
	
	@Test
	public void IdAuthUncheckedExceptionTest2() throws IdAuthenticationBaseException {
		assertThrows(IdAuthUncheckedException.class, () -> {
		throw new IdAuthUncheckedException("abcd", "message");
			});
	}
	
	@Test
	public void IdAuthUncheckedExceptionTest3() throws IdAuthenticationBaseException {
		assertThrows(IdAuthUncheckedException.class, () -> {
		throw new IdAuthUncheckedException("abcd", "message", new IOException());
			});
	}
	
	@Test
	public void IdAuthUncheckedExceptionTest4() throws IdAuthenticationBaseException {
		assertThrows(IdAuthUncheckedException.class, () -> {
		throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR);
			});
	}
	
	@Test
	public void IdAuthUncheckedExceptionTest5() throws IdAuthenticationBaseException {
		assertThrows(IdAuthUncheckedException.class, () -> {
		throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR, new IOException());
			});
	}

}
