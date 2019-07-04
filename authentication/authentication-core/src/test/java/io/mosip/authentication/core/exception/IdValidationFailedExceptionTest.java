package io.mosip.authentication.core.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * The Class IdValidationFailedExceptionTest.
 *
 * @author Manoj SP
 */
@RunWith(MockitoJUnitRunner.class)
public class IdValidationFailedExceptionTest {

	/** The auth req. */
	AuthRequestDTO authReq = new AuthRequestDTO();

	/** The errors. */
	private Errors errors = new org.springframework.validation.BindException(authReq, "authReq");
	
	/**
	 * Test data validation exception default cons.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationExceptionDefaultCons() throws IdValidationFailedException {
		errors.rejectValue(null, "test error", "test error");
		throw new IdValidationFailedException();
	}

	/**
	 * Test data validation exception.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationException() throws IdValidationFailedException {
		errors.rejectValue(null, "test error", "test error");
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, errors);
	}

	/**
	 * Test data validation exception with errors.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationExceptionWithErrors() throws IdValidationFailedException {
		throw new IdValidationFailedException(errors);
	}
	
	/**
	 * Test data validation exception 1.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationException1() throws IdValidationFailedException {
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, new Throwable());
	}

	/**
	 * Test data validation exception with errors 1.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testDataValidationExceptionWithErrors1() throws IdValidationFailedException {
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}

}
