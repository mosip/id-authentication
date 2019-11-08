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
	VID_NOT_AVAILABLE("KER-VID-001", "VID not available for allocation"),
	/**
	 * VID_STATUS_NOT_FOUND
	 */
	VID_STATUS_NOT_FOUND("KER-VID-002", "Given VID status not found"),
	/**
	 * VID_NOT_ISSUED
	 */
	VID_NOT_ISSUED("KER-VID-003", "Given VID is not in ISSUED status"),
	/**
	 * VID_NOT_ISSUED
	 */
	VID_EXPIRY_DATE_EMPTY("KER-VID-004", "Vid expiry date is empty"),
	/**
	 * VID_NOT_ISSUED
	 */
	VID_EXPIRY_DATE_INVALID("KER-VID-005", "Vid expiry date is before current utc time"),
	/**
	 * VID_NOT_ISSUED
	 */
	VID_EXPIRY_DATE_PATTERN_INVALID("KER-VID-006", "Vid expiry date pattern is invalid, should be in yyyy-MM-dd'T'HH:mm:ss.SSS'Z' pattern"),
	/**
	 * INTERNAL_SERVER_ERROR
	 */
	INTERNAL_SERVER_ERROR("KER-VID-007", "Internal Server Error"),
	CONFIG_SERVER_FETCH_FAILED("KER-VID-008", "Error in retrieving from config server");

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
