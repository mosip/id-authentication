package io.mosip.admin.navigation.constant;

/**
 * Constants for Login related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum LoginErrorCode {

    EMPTY_COOKIE("ADMIN-XXXX", "Cookie is empty");

    private final String errorCode;
    private final String errorMessage;

    private LoginErrorCode(final String errorCode, final String errorMessage) {
	this.errorCode = errorCode;
	this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
	return errorCode;
    }

    public String getErrorMessage() {
	return errorMessage;
    }
}
