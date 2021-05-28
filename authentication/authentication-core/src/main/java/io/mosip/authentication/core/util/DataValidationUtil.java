package io.mosip.authentication.core.util;

import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;

/**
 * The Class DataValidationUtil - Checks for errors in the error object
 * and throws {@link IDDataValidationException}, if any error is present.
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
	 * Get list of errors from error object and throws
	 * {@link IDDataValidationException}, if any error is present.
	 *
	 * @param errors the errors
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public static void validate(Errors errors) throws IDDataValidationException {
		if (errors.hasErrors()) {
			IDDataValidationException exception = new IDDataValidationException();
			exception.clearArgs();
			errors.getAllErrors()
					.forEach(error -> {
						String errorCode = error.getCode();
						String errorMessage = error.getDefaultMessage();
						String actionMesgsage = IdAuthenticationErrorConstants.getActionMessageForErrorCode(error.getCode()).orElse(null);
						Object[] args = error.getArguments();
						exception.addInfo(errorCode, errorMessage, actionMesgsage, args);
					});
			throw exception;
		}
	}

}
