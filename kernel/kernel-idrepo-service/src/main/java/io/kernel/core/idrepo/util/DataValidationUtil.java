package io.kernel.core.idrepo.util;

import org.springframework.validation.Errors;

import io.kernel.core.idrepo.exception.IdRepoDataValidationException;

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
	 * {@link IdRepoDataValidationException}, if any error is present.
	 *
	 * @param errors
	 *            the errors
	 * @throws IdRepoDataValidationException
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
