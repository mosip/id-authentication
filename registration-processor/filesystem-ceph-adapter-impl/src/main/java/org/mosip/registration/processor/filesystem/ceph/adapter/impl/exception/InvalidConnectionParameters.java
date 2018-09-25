package org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;


import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.utils.IISPlatformErrorCodes;

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
		super(IISPlatformErrorCodes.IIS_EPU_ATU_INVALID_CONNECTION_PARAMETERS, message);
	}

	public InvalidConnectionParameters(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_INVALID_CONNECTION_PARAMETERS + EMPTY_SPACE, message, cause);
	}
}
