package io.mosip.idrepository.core.test.exception;

import org.junit.Test;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.AuthenticationException;

/**
 * 
 * @author Prem Kumar
 *
 */
public class AuthenticationExceptionTest {

	
	@Test(expected =AuthenticationException.class)
	public void testAuthenticationAppException() throws AuthenticationException{
		throw new AuthenticationException();
	}
	/**
	 * Test Authentication Exception with err code and msg.
	 *
	 * @throws AuthenticationException the Authentication Exception
	 */
	@Test(expected = AuthenticationException.class)
	public void testAuthenticationExceptionWithErrCodeAndMsg() throws AuthenticationException {
		throw new AuthenticationException("code", "msg",1);
	}

	/**
	 * Test Authentication Exception with err code and msg and cause.
	 *
	 * @throws AuthenticationException the Authentication Exception
	 */
	@Test(expected = AuthenticationException.class)
	public void testAuthenticationExceptionWithErrCodeAndMsgAndCause() throws AuthenticationException {
		throw new AuthenticationException("code", "msg", new AuthenticationException(),1);
	}

	/**
	 * Test Authentication Exception with constant.
	 *
	 * @throws AuthenticationException the Authentication Exception
	 */
	@Test(expected = AuthenticationException.class)
	public void testAuthenticationExceptionWithConstant() throws AuthenticationException {
		throw new AuthenticationException(IdRepoErrorConstants.INVALID_REQUEST,1);
	}

	/**
	 * Test Authentication Exception with constant and cause.
	 *
	 * @throws AuthenticationException the Authentication Exception
	 */
	@Test(expected = AuthenticationException.class)
	public void testAuthenticationExceptionWithConstantAndCause() throws AuthenticationException {
		throw new AuthenticationException(IdRepoErrorConstants.INVALID_REQUEST, new AuthenticationException(),1);
	}

	/**
	 * Test Authentication Exception with constant and id.
	 *
	 * @throws AuthenticationException the Authentication Exception
	 */
	@Test(expected = AuthenticationException.class)
	public void testAuthenticationExceptionWithConstantAndId() throws AuthenticationException {
		throw new AuthenticationException(IdRepoErrorConstants.INVALID_REQUEST,1);
	}

	/**
	 * Test Authentication Exception with constant and cause and id.
	 *
	 * @throws AuthenticationException the Authentication Exception
	 */
	@Test(expected = AuthenticationException.class)
	public void testAuthenticationExceptionWithConstantAndCauseAndId() throws AuthenticationException {
		throw new AuthenticationException(IdRepoErrorConstants.INVALID_REQUEST, new AuthenticationException(),1);
	}
}
