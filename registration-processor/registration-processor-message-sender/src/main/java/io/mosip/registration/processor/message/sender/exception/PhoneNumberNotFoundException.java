package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class PhoneNumberNotFoundException.
 */
public class PhoneNumberNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new phone number not found exception.
	 */
	public PhoneNumberNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new phone number not found exception.
	 *
	 * @param message
	 *            the message
	 */
	public PhoneNumberNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode(), message);
	}

	/**
	 * Instantiates a new phone number not found exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public PhoneNumberNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
