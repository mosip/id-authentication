package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

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
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionEnumThrowable() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
