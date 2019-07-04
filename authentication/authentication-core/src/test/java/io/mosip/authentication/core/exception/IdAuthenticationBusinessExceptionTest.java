package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Class IdAuthenticationBusinessExceptionTest.
 *
 * @author Manoj SP
 */
public class IdAuthenticationBusinessExceptionTest {
	
	/**
	 * Id authentication business exception default cons.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionDefaultCons() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException();
	}

	/**
	 * Id authentication business exception 2 args.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessException2args() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException("errorcode", "errormessage");
	}
	
	/**
	 * Id authentication business exception 3 args.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessException3args() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException("errorcode", "errormessage", null);
	}
	
	/**
	 * Id authentication business exception enum.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionEnum() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}
	
	/**
	 * Id authentication business exception enum throwable.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	public void IdAuthenticationBusinessExceptionEnumThrowable() throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
	}

}
