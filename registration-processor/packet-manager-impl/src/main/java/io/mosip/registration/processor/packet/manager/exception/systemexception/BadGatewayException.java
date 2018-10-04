package io.mosip.registration.processor.packet.manager.exception.systemexception;



import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * This is BadGatewayException.
 *
 */
public class BadGatewayException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public BadGatewayException() {
		super();
	}

	public BadGatewayException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_BAD_GATEWAY, message);
	}

	public BadGatewayException(String message,Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_BAD_GATEWAY + EMPTY_SPACE, message,cause);
	}
}
