package io.mosip.registration.processor.request.handler.upload.utils;

import org.springframework.validation.Errors;

import io.mosip.registration.processor.request.handler.service.exception.PacketGeneratorValidationException;


/**
 * The Class PacketGeneratorValidationUtil.
 * @author Rishabh Keshari
 */
public final class PacketGeneratorValidationUtil {

	
	/**
	 * Instantiates a new packet generator validation util.
	 */
	private PacketGeneratorValidationUtil() {
	}

	
	/**
	 * Validate.
	 *
	 * @param errors the errors
	 * @throws PacketGeneratorValidationException the packet generator validation exception
	 */
	public static void validate(Errors errors) throws PacketGeneratorValidationException {
		if (errors.hasErrors()) {
			PacketGeneratorValidationException exception = new PacketGeneratorValidationException();
			errors.getAllErrors().forEach(error -> exception.addInfo(error.getCode(), error.getDefaultMessage()));
			throw exception;
		}
	}

}
