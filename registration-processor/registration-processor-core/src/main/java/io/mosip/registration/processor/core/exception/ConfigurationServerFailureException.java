package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.errorcodes.AbstractVerticleErrorCodes;

public class ConfigurationServerFailureException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurationServerFailureException() {
		super();
	}

	public ConfigurationServerFailureException(String message) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_CONFIGURATION_SERVER_FAILURE_EXCEPTION, message);
	}

	public ConfigurationServerFailureException(String message, Throwable cause) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_CONFIGURATION_SERVER_FAILURE_EXCEPTION + EMPTY_SPACE, message,
				cause);
	}
}
