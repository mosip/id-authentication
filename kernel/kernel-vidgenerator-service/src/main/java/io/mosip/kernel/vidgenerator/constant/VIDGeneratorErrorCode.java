package io.mosip.kernel.vidgenerator.constant;

/**
 * Error Code for VID generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum VIDGeneratorErrorCode {
	/**
	 * VID_NOT_FOUND
	 */
	VID_NOT_FOUND("KER-VIG-001", "VID could not be found"),
	/**
	 * VID_STATUS_NOT_FOUND
	 */
	VID_STATUS_NOT_FOUND("KER-VIG-003", "Given VID status not found"),
	/**
	 * VID_NOT_ISSUED
	 */
	VID_NOT_ISSUED("KER-VIG-004", "Given VID is not in ISSUED status"),
	/**
	 * INTERNAL_SERVER_ERROR
	 */
	INTERNAL_SERVER_ERROR("KER-VIG-005", "Internal Server Error"),
	CONFIG_SERVER_FETCH_FAILED("KER-VIG-006", "Error in retrieving from config server");

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
	private VIDGeneratorErrorCode(final String errorCode, final String errorMessage) {
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
