package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.storage.exception.code.PacketMetaInfoErrorCode;

public class IdentityNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public IdentityNotFoundException() {
		super();
	}
	
	public IdentityNotFoundException(String errorMessage) {
		super(PacketMetaInfoErrorCode.IDENTITY_NOT_FOUND+ EMPTY_SPACE, errorMessage);
	}

	public IdentityNotFoundException(String message, Throwable cause) {
		super(PacketMetaInfoErrorCode.IDENTITY_NOT_FOUND + EMPTY_SPACE, message, cause);
	}

}
