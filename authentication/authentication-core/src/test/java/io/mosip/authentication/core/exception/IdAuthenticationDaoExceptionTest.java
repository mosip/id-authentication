package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

/**
 * The Class IdAuthenticationDaoExceptionTest.
 *
 * @author Manoj SP
 */
public class IdAuthenticationDaoExceptionTest {
	
	/**
	 * Id authentication dao exception default cons.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoExceptionDefaultCons() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException();
	}

	/**
	 * Id authentication dao exception 2 args.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoException2args() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException("errorcode", "errormessage");
	}
	
	/**
	 * Id authentication dao exception 3 args.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoException3args() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException("errorcode", "errormessage", null);
	}
	
	/**
	 * Id authentication dao exception enum.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoExceptionEnum() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}
	
	/**
	 * Id authentication dao exception enum throwable.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test(expected=IdAuthenticationDaoException.class)
	public void IdAuthenticationDaoExceptionEnumThrowable() throws IdAuthenticationDaoException {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}

}
