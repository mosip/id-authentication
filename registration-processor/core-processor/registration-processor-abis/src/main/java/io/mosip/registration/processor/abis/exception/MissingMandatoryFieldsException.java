package io.mosip.registration.processor.abis.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class MissingMandatoryFieldsException extends BaseUncheckedException {

	public MissingMandatoryFieldsException(String message, Throwable cause) {
		super(PlatformErrorMessages.MISSING_MANDATORY_FIELDS.getCode() + EMPTY_SPACE, message, cause);

	}

	public MissingMandatoryFieldsException(String errorMessage) {
		super(PlatformErrorMessages.MISSING_MANDATORY_FIELDS.getCode(), errorMessage);
		
	}

	public MissingMandatoryFieldsException() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2373121691700625404L;
	
}
