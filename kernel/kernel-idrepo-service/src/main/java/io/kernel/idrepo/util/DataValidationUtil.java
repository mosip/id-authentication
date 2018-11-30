package io.kernel.idrepo.util;

import org.springframework.validation.Errors;

import io.kernel.idrepo.exception.IdRepoDataValidationException;

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
	 * Get list of errors from error object and throws
	 * {@link IDDataValidationException}, if any error is present.
	 *
	 * @param exceptionConstant
	 *            the exception constant
	 * @param errors
	 *            the errors
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 */
	public static void validate(Errors errors) throws IdRepoDataValidationException {
		if (errors.hasErrors()) {
			IdRepoDataValidationException exception = new IdRepoDataValidationException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
