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
	 * Token expired
	 */
	TOKEN_EXPIRED("KER-ATH-402", "Token expired"),
	/**
	 * Invalid Token
	 */
	INVALID_TOKEN("KER-ATH-401", "Invalid Token"),
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
	REQUEST_VALIDATION_ERROR("KER-ATH-004", "Error while validating the request"),
	/**
	 * USER VALIDATION ERROR
	 */
	USER_VALIDATION_ERROR("KER-ATH-003", "User Detail doesn't exist"),

	/**
	 * PASSWORD VALIDATION ERROR
	 */
	PASSWORD_VALIDATION_ERROR("KER-ATH-005", "Incorrect Password"),

	/**
	 * Empty Cookie error
	 */
	COOKIE_NOTPRESENT_ERROR("KER-ATH-006", "Cookies are empty"),
	/**
	 * Empty Cookie error
	 */
	TOKEN_NOTPRESENT_ERROR("KER-ATH-007", "Token is not present in cookies"),
	/**
	 * Token Datastore error
	 */
	TOKEN_DATASTORE_ERROR("KER-ATH-008", "Token is not present in datastore,Please try with new token"),
	/**
	 * Client error
	 */
	SERVER_ERROR("KER-ATH-500", "Server error occured,Please check the logs "),
	
	
	TEMPLATE_ERROR("KER-ATH-436", " Template is missing for language "),
	
	/**
	 * Client error
	 */
	CLIENT_ERROR("KER-ATH-501", "Client error occured with message "),

	/**
	 * Empty Cookie error
	 */
	INVALID_DATASOURCE_ERROR("KER-ATH-008", "Invalid datasource and Please check the application id"),

	NAMING_EXCEPTION("KER-ATH-009", "Exception occued while unblocking the account"),
	
	UNABLE_CLOSE_LDAP_CONTEXT("KER-ATH-010","Exception occured while closing the context"),
	
	PASSWORD_POLICY_EXCEPTION("KER-ATH-011","Password that is entered does not meet the password policy"),
	
	OLD_PASSWORD_NOT_MATCH("KER-ATH-012","Old password entered is incorrect"),
	
	USER_ALREADY_EXIST("KER-ATH-013","Username already exist in datastore"),
	
	ROLE_NOT_FOUND("KER-ATH-014","Exception occured while creating a user "),
	
	USER_CREATE_EXCEPTION("KER-ATH-015","Exception occured while creating a user "),
	
	USER_PASSWORD_EXCEPTION("KER-ATH-016","Exception occured while inserting a password for user "),
	
	ROLLBACK_USER_EXCEPTION("KER-ATH-017","Exception occured while rolling back user"),
	
	INVALID_DN("KER-ATH-018","Exception occured while creating DN"), 
	INVALID_REQUEST("KER-ATH-019","should not be null or empty");
	
	

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
