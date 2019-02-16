package io.mosip.registration.processor.status.utilities;

import org.springframework.validation.Errors;

import io.mosip.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;

/**
 * The Class DataValidationUtil.
 *
 * @author Manoj SP
 */
public final class RegStatusValidationUtil {

	/**
	 * Instantiates a new data validation util.
	 */
	private RegStatusValidationUtil() {
	}

	/**
	 * Get list of errors from error object
	 *
	 * @param errors
	 *            the errors
	 * @throws IdRepoDataValidationException
	 *             the IdRepoDataValidationException
	 */
	public static void validate(Errors errors) throws RegStatusValidationException {
		if (errors.hasErrors()) {
			RegStatusValidationException exception = new RegStatusValidationException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
