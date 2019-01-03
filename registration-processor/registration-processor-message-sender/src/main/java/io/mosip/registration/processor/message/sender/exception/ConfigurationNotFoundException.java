package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ConfigurationNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public ConfigurationNotFoundException() {
		super();
	}

	public ConfigurationNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode(), message);
	}

	public ConfigurationNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
