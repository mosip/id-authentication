package io.mosip.kernel.otpmanager.constant;

/**
 * This enum provides all the constants for OTP expiration attributes.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @version 1.0.0
 *
 */
public enum OtpExpiryConstants {
	OTP_PROPERTIES_FILE_NAME("otp"), OTP_EXPIRY_TIME_LIMIT("mosip.otp.expiry.time"), USER_FREEZE_DURATION(
			"mosip.key.freeze.time"), ALLOWED_NUMBER_OF_ATTEMPTS(
					"mosip.validation.attempt.count"), DEFAULT_NUM_OF_ATTEMPT(0);

	/**
	 * The property for the constant of type integer.
	 */
	private int property;

	/**
	 * The property for the constant of type string.
	 */
	private String stringProperty;

	/**
	 * Constructor that accepts integer parameters.
	 * 
	 * @param property
	 *            The integer property to be set.
	 * 
	 */
	private OtpExpiryConstants(int property) {
		this.property = property;
	}

	/**
	 * Constructor that accepts String parameters.
	 * 
	 * @param stringProperty
	 *            The String property to be set.
	 * 
	 */
	private OtpExpiryConstants(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	/**
	 * Setter for String property.
	 * 
	 * @return The String property.
	 * 
	 */
	public String getStringProperty() {
		return stringProperty;
	}

	/**
	 * Getter for integer property.
	 * 
	 * @return The integer property.
	 * 
	 */
	public int getProperty() {
		return property;
	}
}
