/*
 * 
 * 
 * 
 * 
 */
package io.mosip.admin.usermgmt.constant;

/**
 * Error Constants for Crypto-Manager-Service
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public enum UserMgmtErrorCode {
	
	
	INTERNAL_SERVER_ERROR("ADM-UMT-500", "Internal server error occured : ");

	/**
	 * The errorCode
	 */
	private final String errorCode;
	/**
	 * The errorMessage
	 */
	private final String errorMessage;

	/**
	 * {@link UserMgmtErrorCode} constructor
	 * 
	 * @param errorCode    error code
	 * @param errorMessage error message
	 */
	private UserMgmtErrorCode(final String errorCode, final String errorMessage) {
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
