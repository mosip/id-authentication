package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class EmailIdNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public EmailIdNotFoundException() {
		super();
	}

	public EmailIdNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_EML_EMAILID_NOT_FOUND.getCode(), message);
	}

	public EmailIdNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_EML_EMAILID_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
