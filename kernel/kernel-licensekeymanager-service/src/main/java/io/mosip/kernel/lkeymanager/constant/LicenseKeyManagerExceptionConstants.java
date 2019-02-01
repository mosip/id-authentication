package io.mosip.kernel.lkeymanager.constant;

public enum LicenseKeyManagerExceptionConstants {
	ILLEGAL_INPUT_ARGUMENTS("KER-LKM-001", "BAD REQUEST : Input values has null or empty values."), 
	INVALID_GENERATED_LICENSEKEY("KER-LKM-002", "The length of license key generated was not of the specified length."),
	NOT_ACCEPTABLE_PERMISSION("KER-LKM-003","Permission value not accepted.");
	
	/**
	 * The error code. 
	 */
	private final String errorCode;

	/**
	 * The error message.
	 */
	private final String errorMessage;

	private LicenseKeyManagerExceptionConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return The error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
