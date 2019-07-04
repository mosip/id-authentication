package io.mosip.kernel.tokenidgenerator.constant;

/**
 * Error Code for Uin generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum TokenIDGeneratorErrorCode {
	INTERNAL_SERVER_ERROR("KER-UIG-005", "Internal Server Error"),
	EMPTY_UIN_OR_PARTNERCODE_EXCEPTION("KER-TIG-010","UIN and partner code cannot be empty"),
	RUNTIME_EXCEPTION("KER-RIG-500","");

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
	private TokenIDGeneratorErrorCode(final String errorCode, final String errorMessage) {
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
