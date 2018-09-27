package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class SecurityFailedExceptionTest {

	@Test(expected=SecurityFailedException.class)
	public void SecurityFailedExceptionEnum() throws SecurityFailedException {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=SecurityFailedException.class)
	public void SecurityFailedExceptionEnumThrowable() throws SecurityFailedException {
		throw new SecurityFailedException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}
	
	@Test(expected=SecurityFailedException.class)
	public void SecurityFailedExceptionDefCons() throws SecurityFailedException {
		throw new SecurityFailedException();
	}

}
