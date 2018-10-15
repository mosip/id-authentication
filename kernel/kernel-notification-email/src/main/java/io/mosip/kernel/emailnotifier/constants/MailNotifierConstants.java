package io.mosip.kernel.emailnotifier.constants;

/**
 * ENUM that provides with the constants for mail notifier.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public enum MailNotifierConstants {
	MESSAGE_REQUEST_SENT("Email Request submitted"),
	ERROR_CODE("ERROR-CODE"),
	LOGGER_TARGET("System.err"),
	EMPTY_STRING("");
	
	/**
	 * The value.
	 */
	private String value;

	/**
	 * Private constructor for {@link MailNotifierConstants}
	 * 
	 * @param message the message.
	 */
	private MailNotifierConstants(String message) {
		this.value = message;
	}

	/**
	 * Getter for value.
	 * 
	 * @return the value.
	 */
	public String getValue() {
		return value;
	}
}
