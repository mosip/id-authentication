/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.auth.login.service.constant;

/**
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public enum AuthDemoServiceErrorCode {
	
	KEYMANAGER_SERVICE_ERROR("KER-DMO-002", "AuditManager Service has replied with following error"),
	/**
	 * 
	 */
	RESPONSE_PARSE_ERROR("KER-DMO-001", "Error occur while parsing error from response");
	

	/**
	 * The errorCode
	 */
	private final String errorCode;
	/**
	 * The errorMessage
	 */
	private final String errorMessage;

	/**
	 * {@link AuthDemoServiceErrorCode} constructor
	 * 
	 * @param errorCode
	 *            error code
	 * @param errorMessage
	 *            error message
	 */
	private AuthDemoServiceErrorCode(final String errorCode, final String errorMessage) {
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
