package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Request input related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RequestErrorCode {

	REQUEST_DATA_NOT_VALID("KER-MSD-999", "Invalid request input");

	private final String errorCode;
	private final String errorMessage;

	private RequestErrorCode(final String errorCode, final String errorMessage) {
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
