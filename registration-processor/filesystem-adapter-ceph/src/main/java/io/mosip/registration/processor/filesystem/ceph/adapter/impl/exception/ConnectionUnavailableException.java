package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ConnectionUnavailableException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public ConnectionUnavailableException() {
		super();
	}

	public ConnectionUnavailableException(String message) {
		super(PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getCode(), message);
	}

	public ConnectionUnavailableException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getCode() + EMPTY_SPACE, message, cause);
	}
}
