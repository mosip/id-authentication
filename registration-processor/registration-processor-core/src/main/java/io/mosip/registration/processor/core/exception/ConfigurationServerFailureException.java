package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ConfigurationServerFailureException.
 */
public class ConfigurationServerFailureException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new configuration server failure exception.
	 */
	public ConfigurationServerFailureException() {
		super();
	}

	/**
	 * Instantiates a new configuration server failure exception.
	 *
	 * @param message the message
	 */
	public ConfigurationServerFailureException(String message) {
		super(PlatformErrorMessages.RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION.getCode(), message);
	}

	/**
	 * Instantiates a new configuration server failure exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ConfigurationServerFailureException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION.getCode() + EMPTY_SPACE, message,
				cause);
	}
}
