package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class IdAuthenticationBusinessExceptionTest {
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionDefaultCons() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException();
	}

	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessException2args() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException("errorcode", "errormessage");
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessException3args() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException("errorcode", "errormessage", null);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionEnum() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionEnumThrowable() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
