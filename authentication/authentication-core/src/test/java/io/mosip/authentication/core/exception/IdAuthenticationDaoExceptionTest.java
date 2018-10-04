package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

public class IdAuthenticationDaoExceptionTest {
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoExceptionDefaultCons() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException();
	}

	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoException2args() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException("errorcode", "errormessage");
	}
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoException3args() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException("errorcode", "errormessage", null);
	}
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoExceptionEnum() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoExceptionEnumThrowable() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
