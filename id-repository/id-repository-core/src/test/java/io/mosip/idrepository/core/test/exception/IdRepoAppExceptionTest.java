package io.mosip.idrepository.core.test.exception;

import org.junit.Test;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;

/**
 * The Class IdRepoAppExceptionTest.
 *
 * @author Manoj SP
 */
public class IdRepoAppExceptionTest {

	/**
	 * Test id repo app exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppException() throws IdRepoAppException {
		throw new IdRepoAppException();
	}

	/**
	 * Test id repo app exception with err code and msg.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithErrCodeAndMsg() throws IdRepoAppException {
		throw new IdRepoAppException("code", "msg");
	}

	/**
	 * Test id repo app exception with err code and msg and cause.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithErrCodeAndMsgAndCause() throws IdRepoAppException {
		throw new IdRepoAppException("code", "msg", new IdRepoAppException());
	}

	/**
	 * Test id repo app exception with constant.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithConstant() throws IdRepoAppException {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST);
	}

	/**
	 * Test id repo app exception with constant and cause.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithConstantAndCause() throws IdRepoAppException {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST, new IdRepoAppException());
	}

	/**
	 * Test id repo app exception with constant and id.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithConstantAndId() throws IdRepoAppException {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST);
	}

	/**
	 * Test id repo app exception with constant and cause and id.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithConstantAndCauseAndId() throws IdRepoAppException {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST, new IdRepoAppException());
	}
	
	/**
	 * 
	 * @throws IdRepoAppException
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithOperationConstantAndCauseAndMessage() throws IdRepoAppException {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST, new IdRepoAppException(),"create");
	}
	
	/**
	 * 
	 * @throws IdRepoAppException
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithOperationAndId() throws IdRepoAppException {
		throw new IdRepoAppException("err","msg", "create");
	}
	
	/**
	 * 
	 * @throws IdRepoAppException
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithOperationConstant() throws IdRepoAppException {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST, "create");
	}
	
	/**
	 * 
	 * 
	 * @throws IdRepoAppException
	 */
	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppExceptionWithOperationConstantAndCauseAndId() throws IdRepoAppException {
		throw new IdRepoAppException("err","msg",new IdRepoAppException(), "create");
	}
	
}
