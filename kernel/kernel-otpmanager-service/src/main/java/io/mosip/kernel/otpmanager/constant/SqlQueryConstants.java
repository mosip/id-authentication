package io.mosip.kernel.otpmanager.constant;

/**
 * This ENUM provides all the constants required for SQL query operations.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @version 1.0.0
 *
 */
public enum SqlQueryConstants {
	UPDATE("UPDATE"), ID("id"), NEW_OTP_STATUS("newOtpStatus"), NEW_NUM_OF_ATTEMPT("newNumOfAttempt"),
	NEW_VALIDATION_TIME("newValidationTime");

	/**
	 * The property.
	 * 
	 */
	private final String property;

	/**
	 * Constructor for SqlQueryConstants class.
	 * 
	 * @param property The property to be set.
	 * 
	 */
	private SqlQueryConstants(String property) {
		this.property = property;
	}

	/**
	 * Getter for property.
	 * 
	 * @return The property.
	 * 
	 */
	public String getProperty() {
		return property;
	}
}
