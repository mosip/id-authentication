package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

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
	@Test
	public void IdAuthenticationDaoExceptionDefaultCons() throws IdAuthenticationDaoException {
		assertThrows(IdAuthenticationDaoException.class, () -> {
		throw new IdAuthenticationDaoException();
			});
	}

	/**
	 * Id authentication dao exception 2 args.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test
	public void IdAuthenticationDaoException2args() throws IdAuthenticationDaoException {
		assertThrows(IdAuthenticationDaoException.class, () -> {
		throw new IdAuthenticationDaoException("errorcode", "errormessage");
			});
	}
	
	/**
	 * Id authentication dao exception 3 args.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test
	public void IdAuthenticationDaoException3args() throws IdAuthenticationDaoException {
		assertThrows(IdAuthenticationDaoException.class, () -> {
		throw new IdAuthenticationDaoException("errorcode", "errormessage", null);
			});
	}
	
	/**
	 * Id authentication dao exception enum.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test
	public void IdAuthenticationDaoExceptionEnum() throws IdAuthenticationDaoException {
		assertThrows(IdAuthenticationDaoException.class, () -> {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}
	
	/**
	 * Id authentication dao exception enum throwable.
	 *
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	@Test
	public void IdAuthenticationDaoExceptionEnumThrowable() throws IdAuthenticationDaoException {
		assertThrows(IdAuthenticationDaoException.class, () -> {
		throw new IdAuthenticationDaoException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
			});
	}

}
