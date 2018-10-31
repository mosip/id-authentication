package io.mosip.registration.processor.packet.manager.exception.systemexception;



import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * ServiceUnavailableException occurs when service is not available.
 *
 */
public class ServiceUnavailableException extends BaseUncheckedException{

	private static final long serialVersionUID = 1L;

	public ServiceUnavailableException() {
		super();
	}

	public ServiceUnavailableException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_SERVICE_UNAVAILABLE, message);
	}

	public ServiceUnavailableException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_SERVICE_UNAVAILABLE + EMPTY_SPACE, message, cause);
	}

}
