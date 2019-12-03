package io.mosip.kernel.pridgenerator.constant;

/**
 * Error Code for VID generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum PRIDGeneratorErrorCode {
	/**
	 * PRID_NOT_FOUND
	 */
	PRID_NOT_AVAILABLE("KER-PRID-001", "PRID not available for allocation"),

	/**
	 * INTERNAL_SERVER_ERROR
	 */
	INTERNAL_SERVER_ERROR("KER-PRID-005", "Internal Server Error"),
	CONFIG_SERVER_FETCH_FAILED("KER-PRID-006", "Error in retrieving from config server");

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
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	private PRIDGeneratorErrorCode(final String errorCode, final String errorMessage) {
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
