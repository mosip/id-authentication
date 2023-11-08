package io.mosip.authentication.authfilter.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import org.junit.Test;

public class IdAuthenticationFilterExceptionTest {

    @Test(expected= IdAuthenticationFilterException.class)
    public void IdAuthenticationFilterException() throws IdAuthenticationFilterException {
        throw new IdAuthenticationFilterException();
    }

    @Test(expected=IdAuthenticationFilterException.class)
    public void IdAuthenticationFilterException2args() throws IdAuthenticationFilterException {
        throw new IdAuthenticationFilterException("errorcode", "errormessage");
    }

    @Test(expected=IdAuthenticationFilterException.class)
    public void IdAuthenticationFilterException3args() throws IdAuthenticationFilterException {
        throw new IdAuthenticationFilterException("errorcode", "errormessage", null);
    }

    @Test(expected=IdAuthenticationFilterException.class)
    public void IdAuthenticationFilterExceptionEnum() throws IdAuthenticationFilterException {
        throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
    }

    @Test(expected=IdAuthenticationFilterException.class)
    public void IdAuthenticationFilterExceptionEnumThrowable() throws IdAuthenticationFilterException {
        throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
    }

}
