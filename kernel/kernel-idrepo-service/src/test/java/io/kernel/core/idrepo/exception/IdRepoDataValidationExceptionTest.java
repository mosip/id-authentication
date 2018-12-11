package io.kernel.core.idrepo.exception;

import org.junit.Test;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * The Class IdRepoDataValidationExceptionTest.
 *
 * @author Manoj SP
 */
public class IdRepoDataValidationExceptionTest {

	/**
	 * Test id repo data validation exception.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoDataValidationException.class)
	public void testIdRepoDataValidationException() throws IdRepoAppException {
		throw new IdRepoDataValidationException();
	}

	/**
	 * Test id repo data validation exception with err code and msg.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoDataValidationException.class)
	public void testIdRepoDataValidationExceptionWithErrCodeAndMsg() throws IdRepoAppException {
		throw new IdRepoDataValidationException("code", "msg");
	}

	/**
	 * Test id repo data validation exception with err code and msg and cause.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoDataValidationException.class)
	public void testIdRepoDataValidationExceptionWithErrCodeAndMsgAndCause() throws IdRepoAppException {
		throw new IdRepoDataValidationException("code", "msg", new IdRepoDataValidationException());
	}

	/**
	 * Test id repo data validation exception with constant.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoDataValidationException.class)
	public void testIdRepoDataValidationExceptionWithConstant() throws IdRepoAppException {
		throw new IdRepoDataValidationException(IdRepoErrorConstants.INVALID_UIN);
	}

	/**
	 * Test id repo data validation exception with constant and cause.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoDataValidationException.class)
	public void testIdRepoDataValidationExceptionWithConstantAndCause() throws IdRepoAppException {
		throw new IdRepoDataValidationException(IdRepoErrorConstants.INVALID_UIN, new IdRepoDataValidationException());
	}

}
