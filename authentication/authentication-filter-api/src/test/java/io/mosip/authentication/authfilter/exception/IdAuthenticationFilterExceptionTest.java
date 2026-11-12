package io.mosip.authentication.authfilter.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import org.junit.jupiter.api.Test;

public class IdAuthenticationFilterExceptionTest {

    @Test
    public void IdAuthenticationFilterException() throws IdAuthenticationFilterException {
		assertThrows(IdAuthenticationFilterException.class, () -> {
        throw new IdAuthenticationFilterException();
    		});
	}

    @Test
    public void IdAuthenticationFilterException2args() throws IdAuthenticationFilterException {
		assertThrows(IdAuthenticationFilterException.class, () -> {
        throw new IdAuthenticationFilterException("errorcode", "errormessage");
    		});
	}

    @Test
    public void IdAuthenticationFilterException3args() throws IdAuthenticationFilterException {
		assertThrows(IdAuthenticationFilterException.class, () -> {
        throw new IdAuthenticationFilterException("errorcode", "errormessage", null);
    		});
	}

    @Test
    public void IdAuthenticationFilterExceptionEnum() throws IdAuthenticationFilterException {
		assertThrows(IdAuthenticationFilterException.class, () -> {
        throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
    		});
	}

    @Test
    public void IdAuthenticationFilterExceptionEnumThrowable() throws IdAuthenticationFilterException {
		assertThrows(IdAuthenticationFilterException.class, () -> {
        throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, null);
    		});
	}

}
