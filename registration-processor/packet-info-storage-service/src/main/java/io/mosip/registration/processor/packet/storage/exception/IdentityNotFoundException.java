package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorConstants;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class IdentityNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public IdentityNotFoundException() {
		super();
	}
	
	public IdentityNotFoundException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getCode()+ EMPTY_SPACE, errorMessage);
	}

	public IdentityNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
