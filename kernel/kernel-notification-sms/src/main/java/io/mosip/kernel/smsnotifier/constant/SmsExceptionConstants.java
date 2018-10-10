package io.mosip.kernel.smsnotifier.constant;

/**
 * This enum provides all the exception constants for sms notification.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
public enum SmsExceptionConstants {

	SMS_ILLEGAL_INPUT("KER-NOS-001", "Number and message can't be empty, null"),
	SMS_NUMBER_INVALID("KER-NOS-002",""),
	SMS_EMPTY_JSON("KER-NOS-003","Json Not Found");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * @param errorCode
	 *            The error code to be set.
	 * @param errorMessage
	 *            The error message to be set.
	 */
	private SmsExceptionConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
