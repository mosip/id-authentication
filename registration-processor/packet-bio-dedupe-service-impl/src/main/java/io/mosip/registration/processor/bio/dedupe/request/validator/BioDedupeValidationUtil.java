package io.mosip.registration.processor.bio.dedupe.request.validator;

import org.springframework.validation.Errors;
import io.mosip.registration.processor.bio.dedupe.exception.BioDedupeValidationException;
/**
 * The Class BioDedupeValidationUtil.
 */
public final class BioDedupeValidationUtil {

	/**
	 * Instantiates a new data validation util.
	 */
	private BioDedupeValidationUtil() {
	}


	/**
	 * Validate.
	 *
	 * @param errors the errors
	 * @throws BioDedupeValidationException the bio dedupe validation exception
	 */
	public static void validate(Errors errors) throws BioDedupeValidationException {
		if (errors.hasErrors()) {
			BioDedupeValidationException exception = new BioDedupeValidationException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
