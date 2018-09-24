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
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=TSPAuthenticationFailedException.class)
	public void TSPAuthenticationFailedExceptionEnumThrowable() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
