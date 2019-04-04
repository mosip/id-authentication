package io.mosip.kernel.otpmanager.constant;

/**
 * This ENUM provides all the constants for OTP status attributes.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @version 1.0.0
 *
 */
public enum OtpStatusConstants {
	UNUSED_OTP("OTP_UNUSED"), KEY_FREEZED("KEY_FREEZED"), BLOCKED_USER("USER_BLOCKED"),
	GENERATION_SUCCESSFUL("GENERATION_SUCCESSFUL"), SET_AS_NULL_IN_STRING("null"), SUCCESS_STATUS("success"),
	SUCCESS_MESSAGE("VALIDATION_SUCCESSFUL"), FAILURE_STATUS("failure"), FAILURE_MESSAGE("VALIDATION_UNSUCCESSFUL"),
	FAILURE_AND_FREEZED_MESSAGE("USER_BLOCKED"), OTP_EXPIRED_STATUS("OTP_EXPIRED"), ADD_SPACE(" ");

	/**
	 * The property.
	 */
	private final String property;

	/**
	 * Setter for property.
	 * 
	 * @param property The property to be set.
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
