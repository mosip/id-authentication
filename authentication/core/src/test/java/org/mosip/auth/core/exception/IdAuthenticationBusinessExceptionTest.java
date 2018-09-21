package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class IdAuthenticationBusinessExceptionTest {

	@Test(expected=IdAuthenticationBusinessException.class)
	public void test1() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException("errorcode", "errormessage");
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void test2() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException("errorcode", "errormessage", null);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void test3() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void test4() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
