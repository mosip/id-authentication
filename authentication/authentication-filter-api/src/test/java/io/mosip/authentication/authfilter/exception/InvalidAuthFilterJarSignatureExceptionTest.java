package io.mosip.authentication.authfilter.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import org.junit.jupiter.api.Test;

public class InvalidAuthFilterJarSignatureExceptionTest {

    @Test
    public void InvalidAuthFilterJarSignatureException() throws InvalidAuthFilterJarSignatureException {
		assertThrows(InvalidAuthFilterJarSignatureException.class, () -> {
        throw new InvalidAuthFilterJarSignatureException();
    		});
	}

    @Test
    public void InvalidAuthFilterJarSignatureException2args() throws InvalidAuthFilterJarSignatureException {
		assertThrows(InvalidAuthFilterJarSignatureException.class, () -> {
        throw new InvalidAuthFilterJarSignatureException("errorcode", "errormessage");
    		});
	}

    @Test
    public void InvalidAuthFilterJarSignatureException3args() throws InvalidAuthFilterJarSignatureException {
		assertThrows(InvalidAuthFilterJarSignatureException.class, () -> {
        throw new InvalidAuthFilterJarSignatureException("errorcode", "errormessage", null);
    		});
	}

    @Test
    public void InvalidAuthFilterJarSignatureExceptionEnum() throws InvalidAuthFilterJarSignatureException {
		assertThrows(InvalidAuthFilterJarSignatureException.class, () -> {
        throw new InvalidAuthFilterJarSignatureException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
    		});
	}

    @Test
    public void InvalidAuthFilterJarSignatureExceptionEnumThrowable() throws InvalidAuthFilterJarSignatureException {
		assertThrows(InvalidAuthFilterJarSignatureException.class, () -> {
        throw new InvalidAuthFilterJarSignatureException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
    		});
	}

}
