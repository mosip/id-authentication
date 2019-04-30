package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ConfigurationNotFoundException.
 */
public class ConfigurationNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new configuration not found exception.
	 */
	public ConfigurationNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new configuration not found exception.
	 *
	 * @param message
	 *            the message
	 */
	public ConfigurationNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode(), message);
	}

	/**
	 * Instantiates a new configuration not found exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ConfigurationNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
