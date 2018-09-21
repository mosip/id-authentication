package org.mosip.auth.core.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.springframework.validation.Errors;

@RunWith(MockitoJUnitRunner.class)
public class IdValidationFailedExceptionTest {

	AuthRequestDTO authReq = new AuthRequestDTO();

	private Errors errors = new org.springframework.validation.BindException(authReq, "authReq");

	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationException() throws IdValidationFailedException {
		errors.rejectValue(null, "test error", "test error");
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, errors);
	}

	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationExceptionWithErrors() throws IdValidationFailedException {
		throw new IdValidationFailedException(errors);
	}
	
	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationException1() throws IdValidationFailedException {
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION, new Throwable());
	}

	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationExceptionWithErrors1() throws IdValidationFailedException {
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.ID_EXPIRED_EXCEPTION);
	}

}
