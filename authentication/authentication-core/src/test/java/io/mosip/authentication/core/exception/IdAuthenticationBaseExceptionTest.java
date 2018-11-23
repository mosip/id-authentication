package io.mosip.authentication.core.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * @author Manoj SP
 *
 */
public class IdAuthenticationBaseExceptionTest {

    @Test(expected = IdAuthenticationBaseException.class)
    public void IdAuthenticationBaseExceptionTest() throws IdAuthenticationBaseException {
	throw new IdAuthenticationBaseException("abcd");
    }

    @Test
    public void testIdAuthenticationBaseExceptionActionCode() throws IdAuthenticationBaseException {
	IdAuthenticationBaseException ex = new IdAuthenticationBaseException(
		IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP);
	assertEquals(IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getActionCode(), ex.getActionCode());
	
    }
}
