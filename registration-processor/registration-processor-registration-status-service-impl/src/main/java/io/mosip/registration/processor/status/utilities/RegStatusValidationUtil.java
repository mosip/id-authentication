package io.mosip.registration.processor.status.utilities;

import org.springframework.validation.Errors;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;

/**
 * The Class RegStatusValidationUtil.
 * @author Rishabh Keshari
 */
public final class RegStatusValidationUtil {

	/**
	 * Instantiates a new data validation util.
	 */
	private RegStatusValidationUtil() {
	}

	/**
	 * Get list of errors from error object.
	 *
	 * @param errors            the errors
	 * @throws RegStatusValidationException the reg status validation exception
	 */
	public static void validate(Errors errors) throws RegStatusValidationException {
		if (errors.hasErrors()) {
			RegStatusValidationException exception = new RegStatusValidationException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
