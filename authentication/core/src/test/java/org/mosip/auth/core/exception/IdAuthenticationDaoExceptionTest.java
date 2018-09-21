package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class IdAuthenticationDaoExceptionTest {

	@Test(expected=IdAuthenticationDaoException.class)
	public void test1() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException("errorcode", "errormessage");
	}
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void test2() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException("errorcode", "errormessage", null);
	}
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void test3() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void test4() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
