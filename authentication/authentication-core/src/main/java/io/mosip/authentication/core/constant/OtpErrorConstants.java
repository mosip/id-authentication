package io.mosip.authentication.core.constant;

/**
 * Error constants for OTP
 * 
 * @author Dinesh Karuppiah.T
 */
public enum OtpErrorConstants {

	/**
	 * Phone Number not registered
	 */
	PHONENOTREGISTERED("KER-SOT-001", "Phone No not registered"),
	/**
	 * Email not registered
	 */
	EMAILNOTREGISTERED("KER-SOT-002", "Email not registered"),

	/**
	 * Email and Phone not registered
	 */
	EMAILPHONENOTREGISTERED("KER-SOT-003", "Both Phone No and E-mail not registered"),

	/**
	 * SMS not configured
	 */
	SMSNOTCONFIGURED("KER-SOT-004", "SMS could not be triggered as it is not a configured channel for notification"),

	/**
	 * EMAIL not configured
	 */
	EMAILNOTCONFIGURED("KER-SOT-005",
			"Email could not be triggered as it is not a configured channel for notification"),

	/**
	 * EMAIL and SMS not configured
	 */
	EMAILSMSNOTCONFIGURED("KER-SOT-006",
			"Email and SMS could not be triggered as it is not a configured channel for notification"),

	/**
	 * EMAIL and SMS not configured
	 */
	UNABLETOSENDNOTIFICATION("KER-SOT-007", "Could not generate/send OTP"),

	/**
	 * User blocked
	 */
	USERBLOCKED("KER-SOT-008", "Not able to Generate OTP for a frozen Account/locked UIN"),

	/**
	 * User blocked
	 */
	LANGUAGENOTCONFIGURED("KER-SOT-009", "Unable to find a primary and/or secondary language configured");
	/**
	 * The error code
	 */
	private final String errorCode;
	/**
	 * The error message
	 */
	private final String errorMessage;

	/**
	 * Constructor to set error code and message
	 * 
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	private OtpErrorConstants(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Function to get error code
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Function to get the error message
	 * 
	 * @return {@link #errorMessage}r
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
