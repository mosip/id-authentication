package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class IdAuthenticationAppExceptionTest {
	
	@Test(expected=IdAuthenticationAppException.class)
	public void IdAuthenticationAppExceptionDefaultcons() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException();
	}

	@Test(expected=IdAuthenticationAppException.class)
	public void IdAuthenticationAppException2args() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException("errorcode", "errormessage");
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void IdAuthenticationAppException3args() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException("errorcode", "errormessage", null);
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void IdAuthenticationAppExceptionConstant() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void IdAuthenticationAppExceptionConstantThrowable() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
