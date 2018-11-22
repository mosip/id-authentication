package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class InstantanceCreationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InstantanceCreationException() {
		super();
	}

	public InstantanceCreationException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getCode() + EMPTY_SPACE, errorMessage);
	}

	public InstantanceCreationException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
