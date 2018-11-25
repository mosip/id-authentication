package io.kernel.core.idrepo.exception;

import org.junit.Test;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * The Class IdRepoUnknownExceptionTest.
 *
 * @author Manoj SP
 */
public class IdRepoUnknownExceptionTest {

	/**
	 * Test id repo unknown exception.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoUnknownException.class)
	public void testIdRepoUnknownException() throws IdRepoAppException {
		throw new IdRepoUnknownException();
	}

	/**
	 * Test id repo unknown exception with err code and msg.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoUnknownException.class)
	public void testIdRepoUnknownExceptionWithErrCodeAndMsg() throws IdRepoAppException {
		throw new IdRepoUnknownException("code", "msg");
	}

	/**
	 * Test id repo unknown exception with err code and msg and cause.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoUnknownException.class)
	public void testIdRepoUnknownExceptionWithErrCodeAndMsgAndCause() throws IdRepoAppException {
		throw new IdRepoUnknownException("code", "msg", new IdRepoUnknownException());
	}

	/**
	 * Test id repo unknown exception with constant.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoUnknownException.class)
	public void testIdRepoUnknownExceptionWithConstant() throws IdRepoAppException {
		throw new IdRepoUnknownException(IdRepoErrorConstants.INVALID_UIN);
	}

	/**
	 * Test id repo unknown exception with constant and cause.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoUnknownException.class)
	public void testIdRepoUnknownExceptionWithConstantAndCause() throws IdRepoAppException {
		throw new IdRepoUnknownException(IdRepoErrorConstants.INVALID_UIN, new IdRepoUnknownException());
	}
}
