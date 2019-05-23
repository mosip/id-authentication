package io.mosip.registration.processor.printing.api.util;

import org.springframework.validation.Errors;

import io.mosip.registration.processor.print.service.exception.RegPrintAppException;

/**
 * The Class PrintServiceValidationUtil.
 * 
 * @author M1048358 Alok
 */
public class PrintServiceValidationUtil {

	private PrintServiceValidationUtil() {
	}

	/**
	 * Validate.
	 *
	 * @param errors            the errors
	 * @throws RegPrintAppException the reg print app exception
	 */
	public static void validate(Errors errors) throws RegPrintAppException {
		if (errors.hasErrors()) {
			RegPrintAppException exception = new RegPrintAppException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}
}
