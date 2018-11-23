package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

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
		super(PlatformErrorMessages.RPR_SYS_UNEXCEPTED_EXCEPTION.getCode(), message);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_UNEXCEPTED_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}
}
