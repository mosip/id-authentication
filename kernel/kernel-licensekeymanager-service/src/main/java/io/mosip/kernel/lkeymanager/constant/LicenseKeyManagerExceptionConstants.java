package io.mosip.kernel.lkeymanager.constant;

/**
 * ENUM to manage constants of exceptions handled in the service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum LicenseKeyManagerExceptionConstants {

	ILLEGAL_TSP("KER-LKM-001", "TSP entered is null or empty."), 
	INVALID_GENERATED_LICENSEKEY("KER-LKM-002","The length of license key generated was not of the specified length."),
	NOT_ACCEPTABLE_PERMISSION("KER-LKM-003", "Permission value entered is not accepted."), 
	LICENSEKEY_NOT_FOUND("KER-LKM-004","LicenseKey Not Found."), 
	LICENSEKEY_EXPIRED("KER-LKM-005", "LicenseKey Expired."),
	ILLEGAL_LICENSE_KEY("KER-LKM-006", "License Key entered is null or empty."),
	ILLEGAL_PERMISSION("KER-LKM-007","Permission entered is an empty string.");

	/**
	 * The error code.
	 */
	private final String errorCode;

	/**
	 * The error message.
	 */
	private final String errorMessage;

	/**
	 * Constructor with error code and error message as the arguments.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	private LicenseKeyManagerExceptionConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return The error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
