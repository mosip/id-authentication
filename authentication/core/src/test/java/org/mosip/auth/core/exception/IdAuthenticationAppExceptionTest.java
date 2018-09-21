package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class IdAuthenticationAppExceptionTest {

	@Test(expected=IdAuthenticationAppException.class)
	public void test1() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException("errorcode", "errormessage");
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void test2() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException("errorcode", "errormessage", null);
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void test3() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void test4() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
