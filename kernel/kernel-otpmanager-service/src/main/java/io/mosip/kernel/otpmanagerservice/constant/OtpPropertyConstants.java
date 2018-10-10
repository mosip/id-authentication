package io.mosip.kernel.otpmanagerservice.constant;

/**
 * This enum defines the constants that holds the OTP properties.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since version 1.0.0
 *
 */
public enum OtpPropertyConstants {
	OTP_FILE("otp"), 
	OTP_LENGTH("otpLength"), 
	OTP_TIME("expireTime"), 
	SHARE_KEY("sharedKey"), 
	KEY_MIN_LENGTH("4"), 
	KEY_MAX_LENGTH("255");

	/**
	 * This variable holds the default OTP properties.
	 */
	private String property;

	/**
	 * Constructor for OtpPropertyConstants enum.
	 * 
	 * @param property
	 *            default properties resource file.
	 */
	OtpPropertyConstants(String property) {
		this.property = property;
	}

	/**
	 * Getter for property.
	 * 
	 * @return default property values.
	 */
	public String getProperty() {
		return property;
	}
}
