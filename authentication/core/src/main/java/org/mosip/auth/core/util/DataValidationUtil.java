package org.mosip.auth.core.util;

import org.mosip.auth.core.exception.IDDataValidationException;
import org.springframework.validation.Errors;

/**
 * The Class DataValidationUtil.
 *
 * @author Manoj SP
 */
public final class DataValidationUtil {

	/**
	 * Instantiates a new data validation util.
	 */
	private DataValidationUtil() {
	}
	
	/**
	 * Get list of errors from error object and throws {@link IDDataValidationException}, if any error is present.
	 *
	 * @param exceptionConstant the exception constant
	 * @param errors the errors
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public static void validate(Errors errors) throws IDDataValidationException {
		if (errors.hasErrors()) {
			IDDataValidationException exception = new IDDataValidationException();
			errors.getFieldErrors()
			.forEach(error -> exception.addInfo(error.getCode(),
					error.getDefaultMessage()));
			throw exception;
		}
	}

}
