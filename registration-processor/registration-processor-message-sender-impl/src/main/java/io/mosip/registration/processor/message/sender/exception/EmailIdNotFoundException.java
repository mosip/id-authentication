package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class EmailIdNotFoundException.
 */
public class EmailIdNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new email id not found exception.
	 */
	public EmailIdNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new email id not found exception.
	 *
	 * @param message
	 *            the message
	 */
	public EmailIdNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_EML_EMAILID_NOT_FOUND.getCode(), message);
	}

	/**
	 * Instantiates a new email id not found exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public EmailIdNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_EML_EMAILID_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}

}
