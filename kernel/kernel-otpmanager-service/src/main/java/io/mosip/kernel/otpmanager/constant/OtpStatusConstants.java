package io.mosip.kernel.otpmanager.constant;

/**
 * This enum provides all the constants for OTP status attributes.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @version 1.0.0
 *
 */
public enum OtpStatusConstants {
	UNUSED_OTP("OTP_UNUSED"), 
	KEY_FREEZED("KEY_FREEZED"), 
	BLOCKED_USER("BLOCKED_USER"), 
	GENERATION_SUCCESSFUL("GENERATION_SUCCESSFUL"),
	SET_AS_NULL_IN_STRING("null"),
	SUCCESS_STATUS("success"),
	SUCCESS_MESSAGE("VALIDATION SUCCESSFUL"),
	FAILURE_STATUS("failure"),
	FAILURE_MESSAGE("VALIDATION UNSUCCESSFUL"),
	ADD_SPACE(" ");

	/**
	 * The property.
	 */
	private final String property;

	/**
	 * Setter for property.
	 * 
	 * @param property
	 *            The property to be set
	 */
	private OtpStatusConstants(String property) {
		this.property = property;
	}

	/**
	 * Getter for property.
	 * 
	 * @return The property.
	 */
	public String getProperty() {
		return property;
	}
}
