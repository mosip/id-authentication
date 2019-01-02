package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class PhoneNumberNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public PhoneNumberNotFoundException() {
		super();
	}

	public PhoneNumberNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode(), message);
	}

	public PhoneNumberNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
