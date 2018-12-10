package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;


import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * InvalidConnectionParameter Exception occurs when
 * connection is attempted with wrong credentials
 *
 */
public class InvalidConnectionParameters extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public InvalidConnectionParameters() {
		super();
	}

	public InvalidConnectionParameters(String message) {
		super(PlatformErrorMessages.RPR_FAC_INVALID_CONNECTION_PARAMETERS.getCode(), message);
	}

	public InvalidConnectionParameters(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_FAC_INVALID_CONNECTION_PARAMETERS.getCode() + EMPTY_SPACE, message, cause);
	}
}
