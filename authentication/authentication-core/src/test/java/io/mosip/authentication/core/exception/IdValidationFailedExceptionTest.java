package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * The Class IdValidationFailedExceptionTest.
 *
 * @author Manoj SP
 */
@ExtendWith(MockitoExtension.class)
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
	@Test
	public void testDataValidationExceptionDefaultCons() throws IdValidationFailedException {
		assertThrows(IdValidationFailedException.class, () -> {
		errors.rejectValue(null, "test error", "test error");
		throw new IdValidationFailedException();
			});
	}

	/**
	 * Test data validation exception.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test
	public void testDataValidationException() throws IdValidationFailedException {
		assertThrows(IdValidationFailedException.class, () -> {
		errors.rejectValue(null, "test error", "test error");
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, errors);
			});
	}

	/**
	 * Test data validation exception with errors.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test
	public void testDataValidationExceptionWithErrors() throws IdValidationFailedException {
		assertThrows(IdValidationFailedException.class, () -> {
		throw new IdValidationFailedException(errors);
			});
	}
	
	/**
	 * Test data validation exception 1.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test
	public void testDataValidationException1() throws IdValidationFailedException {
		assertThrows(IdValidationFailedException.class, () -> {
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, new Throwable());
			});
	}

	/**
	 * Test data validation exception with errors 1.
	 *
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	@Test
	public void testDataValidationExceptionWithErrors1() throws IdValidationFailedException {
		assertThrows(IdValidationFailedException.class, () -> {
		throw new IdValidationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}

}
