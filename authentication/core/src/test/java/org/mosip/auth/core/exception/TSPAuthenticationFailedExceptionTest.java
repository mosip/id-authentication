package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class TSPAuthenticationFailedExceptionTest {

	@Test(expected=TSPAuthenticationFailedException.class)
	public void test3() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=TSPAuthenticationFailedException.class)
	public void test4() throws TSPAuthenticationFailedException {
		throw new TSPAuthenticationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
