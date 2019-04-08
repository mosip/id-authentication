package io.mosip.kernel.licensekeygenerator.misp.constant;

/**
 * ENUM to handle the error codes and error messages passed in exception class
 * constructors.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum MISPLicenseKeyGeneratorConstant {
	/**
	 * Error code and message for generated license key of different length than the
	 * specified one.
	 */
	LENGTH_NOT_SAME("KER-MLK-001", "Length of generated key is not same as the length specified in the configuration");
	/**
	 * The error code.
	 */
	private String errorCode;
	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor to initialize the error code and message.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	private MISPLicenseKeyGeneratorConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
