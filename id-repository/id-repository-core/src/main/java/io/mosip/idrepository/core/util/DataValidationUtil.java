package io.mosip.idrepository.core.util;

import org.springframework.validation.Errors;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;

/**
 * The Class DataValidationUtil - utility to validate {@code Errors} from
 * validator and throw exception compiling all errors.
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
	 * Get list of errors from error object and build and throw {@code IdRepoDataValidationException}.
	 *
	 * @param errors the errors
	 * @throws IdRepoDataValidationException the IdRepoDataValidationException
	 */
	public static void validate(Errors errors) throws IdRepoDataValidationException {
		if (errors.hasErrors()) {
			IdRepoDataValidationException exception = new IdRepoDataValidationException();
			errors.getAllErrors().stream()
					.filter(error -> IdRepoErrorConstants.getAllErrorCodes().contains(error.getCode()))
					.forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
