package io.mosip.registration.processor.packet.receiver.exception.systemexception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * This is unexpected exception.
 *
 */
public class UnexpectedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public UnexpectedException() {
		super();
	}

	public UnexpectedException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_UNEXCEPTED_ERROR, message);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_UNEXCEPTED_ERROR + EMPTY_SPACE, message, cause);
	}
}
