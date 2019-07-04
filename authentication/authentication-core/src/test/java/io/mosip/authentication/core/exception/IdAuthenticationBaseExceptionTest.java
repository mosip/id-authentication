package io.mosip.authentication.core.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
	@Test(expected = IdAuthenticationBaseException.class)
	public void IdAuthenticationBaseExceptionTest1() throws IdAuthenticationBaseException {
		throw new IdAuthenticationBaseException("abcd");
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
