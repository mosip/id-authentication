package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

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
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void IdAuthenticationAppExceptionConstantThrowable() throws IdAuthenticationAppException {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
