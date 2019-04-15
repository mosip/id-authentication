package io.mosip.kernel.auth.constant;

/**
 * Error Code for Auth Service
 * 
 * @author Ramadurai Pandian
 * @since 1.0.0
 *
 */
public enum AuthErrorCode {
	/**
	 * UNAUTHORIZED
	 */
	UNAUTHORIZED("KER-ATH-401", "Authentication Failed"),
	/**
	 * FORBIDDEN
	 */
	FORBIDDEN("KER-ATH-403", "Forbidden"),
	/**
	 * FORBIDDEN
	 */
	CONNECT_EXCEPTION("KER-ATH-002", "Fail to connect to auth service"),
	/**
	 * RESPONSE_PARSE_ERROR
	 */
	RESPONSE_PARSE_ERROR("KER-ATH-001", "Error occur while parsing error from response"),
	/**
	 * RESPONSE_PARSE_ERROR
	 */
	REQUEST_VALIDATION_ERROR("KER-ATH-002", "Error while validating the request"),
	/**
	 * USER VALIDATION ERROR
	 */
	USER_VALIDATION_ERROR("KER-ATH-003", "User Detail doesn't exist"),
	
	/**
	 * PASSWORD VALIDATION ERROR
	 */
	PASSWORD_VALIDATION_ERROR("KER-ATH-004", "Incorrect Password"),
	
	/**
	 * Empty Cookie error
	 */
	COOKIE_NOTPRESENT_ERROR("KER-ATH-005", "Cookies are empty"),
	/**
	 * Empty Cookie error
	 */
	TOKEN_NOTPRESENT_ERROR("KER-ATH-005", "Token is not present in cookies");

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
	private AuthErrorCode(final String errorCode, final String errorMessage) {
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
