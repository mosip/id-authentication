package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class FieldNotFoundException  extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FieldNotFoundException() {
		super();
	}
	
	public FieldNotFoundException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getCode()+ EMPTY_SPACE, errorMessage);
	}

	public FieldNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
