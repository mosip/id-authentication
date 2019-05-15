/*
 * 
 * 
 * 
 * 
 */
package io.mosip.admin.constant;

/**
 * Error Constants for inAdm
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public enum AdminErrorCode {
	
	INVALID_REQUEST("ADM-UMT-002", "should not be null or empty"),
	
	INTERNAL_SERVER_ERROR("ADM-UMT-500", "Internal server error occured : "),
	
	CLIENT_SERVICE_ERROR("ADM-UMT-500","Error occured while calling service");

	/**
	 * The errorCode
	 */
	private final String errorCode;
	/**
	 * The errorMessage
	 */
	private final String errorMessage;

	/**
	 * {@link AdminErrorCode} constructor
	 * 
	 * @param errorCode    error code
	 * @param errorMessage error message
	 */
	private AdminErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode
	 * 
	 * @return errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage
	 * 
	 * @return errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
