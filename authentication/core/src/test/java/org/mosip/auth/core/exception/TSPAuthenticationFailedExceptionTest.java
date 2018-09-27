package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class TSPAuthenticationFailedExceptionTest {

	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionDefCon() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException();
	}
	
	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionEnum() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
	}
	
	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionEnumThrowable() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, null);
	}

}
