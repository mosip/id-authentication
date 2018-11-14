package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.systemexception;


import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;

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
		super(RPRPlatformErrorCodes.RPR_FAC_UNEXCEPTED_ERROR, message);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(RPRPlatformErrorCodes.RPR_FAC_UNEXCEPTED_ERROR + EMPTY_SPACE, message, cause);
	}
}
