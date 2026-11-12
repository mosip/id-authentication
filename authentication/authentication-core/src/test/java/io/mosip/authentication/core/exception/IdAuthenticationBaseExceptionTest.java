package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class IdAuthenticationBaseExceptionTest.
 *
 * @author Manoj SP
 */
public class IdAuthenticationBaseExceptionTest {

	/**
	 * Id authentication base exception test.
	 *
	 * @throws IdAuthenticationBaseException the id authentication base exception
	 */
	@Test
	public void IdAuthenticationBaseExceptionTest1() throws IdAuthenticationBaseException {
		assertThrows(IdAuthenticationBaseException.class, () -> {
		throw new IdAuthenticationBaseException("abcd");
			});
	}

	/**
	 * Test id authentication base exception action code.
	 *
	 * @throws IdAuthenticationBaseException the id authentication base exception
	 */
	@Test
	public void testIdAuthenticationBaseExceptionActionCode() throws IdAuthenticationBaseException {
		IdAuthenticationBaseException ex = new IdAuthenticationBaseException(
				IdAuthenticationErrorConstants.INVALID_TIMESTAMP);
		assertEquals(IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getActionMessage(), ex.getActionMessage());

	}
}
