package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class IdAuthenticationAppExceptionTest.
 *
 * @author Manoj SP
 */
public class IdAuthenticationAppExceptionTest {
	
	/**
	 * Id authentication app exception defaultcons.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test
	public void IdAuthenticationAppExceptionDefaultcons() throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
		throw new IdAuthenticationAppException();
			});
	}

	/**
	 * Id authentication app exception 2 args.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test
	public void IdAuthenticationAppException2args() throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
		throw new IdAuthenticationAppException("errorcode", "errormessage");
			});
	}
	
	/**
	 * Id authentication app exception 3 args.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test
	public void IdAuthenticationAppException3args() throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
		throw new IdAuthenticationAppException("errorcode", "errormessage", null);
			});
	}
	
	/**
	 * Id authentication app exception constant.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test
	public void IdAuthenticationAppExceptionConstant() throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}
	
	/**
	 * Id authentication app exception constant throwable.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test
	public void IdAuthenticationAppExceptionConstantThrowable() throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
		throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
			});
	}

}
