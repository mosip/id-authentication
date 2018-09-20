package org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.utils.IISPlatformErrorCodes;

public class ConnectionUnavailableException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public ConnectionUnavailableException() {
		super();
	}

	public ConnectionUnavailableException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_CONNECTION_NOT_AVAILABLE, message);
	}

	public ConnectionUnavailableException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_CONNECTION_NOT_AVAILABLE + EMPTY_SPACE, message, cause);
	}
}
