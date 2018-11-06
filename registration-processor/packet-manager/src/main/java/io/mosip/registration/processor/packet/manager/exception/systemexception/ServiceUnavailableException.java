package io.mosip.registration.processor.packet.manager.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * ServiceUnavailableException occurs when service is not available.
 *
 */
public class ServiceUnavailableException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new service unavailable exception.
	 */
	public ServiceUnavailableException() {
		super();
	}

	/**
	 * Instantiates a new service unavailable exception.
	 *
	 * @param message the message
	 */
	public ServiceUnavailableException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_SERVICE_UNAVAILABLE, message);
	}

	/**
	 * Instantiates a new service unavailable exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ServiceUnavailableException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_SERVICE_UNAVAILABLE + EMPTY_SPACE, message, cause);
	}

}
