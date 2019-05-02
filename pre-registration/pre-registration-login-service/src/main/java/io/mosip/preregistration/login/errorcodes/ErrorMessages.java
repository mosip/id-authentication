package io.mosip.preregistration.login.errorcodes;

/**
 * This enum provides different error codes 
 * 
 * @author Akshay
 * @since 1.0.0
 */
public enum ErrorMessages {

	SEND_OTP_FAILED("OTP failed to send through a specified channel"),//PRG_AUTH_001
	USERID_OTP_VALIDATION_FAILED("Authentication failed"),//PRG_AUTH_002
	INVALIDATE_TOKEN_FAILED("Failed to invalidate the auth token"),//PRG_AUTH_003
	INVALID_REQUEST_ID("Invlaid Request Id recieved"),//PRG_AUTH_004
	INVALID_REQUEST_VERSION("Invlaid Request version recieved"),//PRG_AUTH_005
	INVALID_REQUEST_DATETIME("Invlaid Request timestamp recieved"),//PRG_AUTH_006
	INVALID_REQUEST_BODY("Invlaid Request recieved"),//PRG_AUTH_007
	INVALID_REQUEST_USERID("Invlaid Request userId recieved"),//PRG_AUTH_008
	INVALID_REQUEST_LANGCODE("Invlaid Request langcode recieved"),//PRG_AUTH_009
	INVALID_REQUEST_OTP("Invlaid Request Id recieved"),//PRE_AUTH_010
	ERROR_WHILE_PARSING("Error while Parsing the kernel response"),//PRE_AUTH_011
	CONFIG_FILE_NOT_FOUND_EXCEPTION("Config file not found in the config server");//PRE_AUTH_011
	private ErrorMessages(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
}