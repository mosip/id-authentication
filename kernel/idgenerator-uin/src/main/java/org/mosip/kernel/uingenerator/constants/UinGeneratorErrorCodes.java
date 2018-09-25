package org.mosip.kernel.uingenerator.constants;

/**
 * Error Code for Uin generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum UinGeneratorErrorCodes {
	UIN_NOT_FOUND("KER-IDG-UIN-001", "Uin could not be found"), UIN_GENERATION_JOB_EXCEPTION("KER-IDG-UIN-002",
			"Error occured in Uin generation job");

	/**
	 * The error code
	 */
	private final String errorCode;
	/**
	 * The error message
	 */
	private final String errorMessage;

	/**
	 * Constructor to set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	private UinGeneratorErrorCodes(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Function to get error code
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Function to get the error message
	 * 
	 * @return {@link #errorMessage}r
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
