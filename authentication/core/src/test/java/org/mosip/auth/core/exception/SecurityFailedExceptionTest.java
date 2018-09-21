package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class SecurityFailedExceptionTest {

	@Test(expected=SecurityFailedException.class)
	public void test3() throws SecurityFailedException {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=SecurityFailedException.class)
	public void test4() throws SecurityFailedException {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
