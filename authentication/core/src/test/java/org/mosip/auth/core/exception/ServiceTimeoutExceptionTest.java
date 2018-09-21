package org.mosip.auth.core.exception;

import org.junit.Test;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

public class ServiceTimeoutExceptionTest {

	@Test(expected=ServiceTimeoutException.class)
	public void test3() throws ServiceTimeoutException {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}
	
	@Test(expected=ServiceTimeoutException.class)
	public void test4() throws ServiceTimeoutException {
		throw new ServiceTimeoutException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, null);
	}

}
