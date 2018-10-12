package io.mosip.kernel.emailnotifier.constants;

/**
 * ENUM that provides with the error codes and messages for mail notifier
 * arguments.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum MailNotifierArgumentErrorConstants {
	RECEIVER_ADDRESS_NOT_FOUND("KER-NOT-MAL-001", "To must be valid. It can't be empty or null."), 
	SUBJECT_NOT_FOUND("KER-NOT-MAL-002", "Subject must be valid. It can't be empty or null."), 
	CONTENT_NOT_FOUND("KER-NOT-MAL-003", "Content must be valid. It can't be empty or null.");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for {@link MailNotifierArgumentErrorConstants}
	 * 
	 * @param errorCode
	 *            this error code.
	 * @param errorMessage
	 *            this error message.
	 */
	private MailNotifierArgumentErrorConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
