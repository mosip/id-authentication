package io.mosip.registration.processor.manual.verification.util;

import org.springframework.validation.Errors;

import io.mosip.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationValidationException;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;

/**
 * The Class ManualVerificationValidationUtil.
 * @author Rishabh Keshari
 */
public final class ManualVerificationValidationUtil {

	/**
	 * Instantiates a new data validation util.
	 */
	private ManualVerificationValidationUtil() {
	}

	/**
	 * Get list of errors from error object.
	 *
	 * @param errors            the errors
	 * @throws ManualVerificationValidationException the manual verification validation exception
	 */
	public static void validate(Errors errors) throws  ManualVerificationValidationException {
		if (errors.hasErrors()) {
			ManualVerificationValidationException exception = new ManualVerificationValidationException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
