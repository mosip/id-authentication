package io.mosip.authentication.core.exception;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.TSPAuthenticationFailedException;

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
