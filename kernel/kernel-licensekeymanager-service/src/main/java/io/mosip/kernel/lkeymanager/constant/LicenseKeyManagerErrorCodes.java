package io.mosip.kernel.lkeymanager.constant;

/**
 * ENUM to manage error codes of exceptions handled in the service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum LicenseKeyManagerErrorCodes {
	HTTP_MESSAGE_NOT_READABLE("KER-LKM-008"), 
	RUNTIME_EXCEPTION("KER-LKM-009");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * Constructor with error code as the argument.
	 * 
	 * @param errorCode
	 *            the error code.
	 */
	private LicenseKeyManagerErrorCodes(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}
}
