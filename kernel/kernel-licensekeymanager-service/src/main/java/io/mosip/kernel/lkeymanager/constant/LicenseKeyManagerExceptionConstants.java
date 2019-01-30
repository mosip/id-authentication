package io.mosip.kernel.lkeymanager.constant;

public enum LicenseKeyManagerExceptionConstants {
	ILLEGAL_INPUT_ARGUMENTS("KER-LKM-001", "Illegal Input");
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
