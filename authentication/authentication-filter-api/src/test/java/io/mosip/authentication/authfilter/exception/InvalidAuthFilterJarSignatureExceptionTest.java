package io.mosip.authentication.authfilter.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import org.junit.Test;

public class InvalidAuthFilterJarSignatureExceptionTest {

    @Test(expected= InvalidAuthFilterJarSignatureException.class)
    public void InvalidAuthFilterJarSignatureException() throws InvalidAuthFilterJarSignatureException {
        throw new InvalidAuthFilterJarSignatureException();
    }

    @Test(expected=InvalidAuthFilterJarSignatureException.class)
    public void InvalidAuthFilterJarSignatureException2args() throws InvalidAuthFilterJarSignatureException {
        throw new InvalidAuthFilterJarSignatureException("errorcode", "errormessage");
    }

    @Test(expected=InvalidAuthFilterJarSignatureException.class)
    public void InvalidAuthFilterJarSignatureException3args() throws InvalidAuthFilterJarSignatureException {
        throw new InvalidAuthFilterJarSignatureException("errorcode", "errormessage", null);
    }

    @Test(expected=InvalidAuthFilterJarSignatureException.class)
    public void InvalidAuthFilterJarSignatureExceptionEnum() throws InvalidAuthFilterJarSignatureException {
        throw new InvalidAuthFilterJarSignatureException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
    }

    @Test(expected=InvalidAuthFilterJarSignatureException.class)
    public void InvalidAuthFilterJarSignatureExceptionEnumThrowable() throws InvalidAuthFilterJarSignatureException {
        throw new InvalidAuthFilterJarSignatureException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
    }

}
