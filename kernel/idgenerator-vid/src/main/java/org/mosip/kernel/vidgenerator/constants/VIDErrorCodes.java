package org.mosip.kernel.vidgenerator.constants;

/**
 * Error Code for VId Generator
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
public enum VIDErrorCodes {
	INVALID_UIN("KER-VID-001", "InValid UIN"), VID_GENERATION_FAILED("KER-VID-002", "VID generation failed");

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
	private VIDErrorCodes(final String errorCode, final String errorMessage) {
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
