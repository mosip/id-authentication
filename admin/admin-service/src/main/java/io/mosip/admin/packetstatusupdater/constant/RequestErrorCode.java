package io.mosip.admin.packetstatusupdater.constant;

/**
 * Constants for Request input related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RequestErrorCode {

	REQUEST_DATA_NOT_VALID("KER-MSD-999", "Invalid request input"),
	REQUEST_INVALID_COLUMN("KER-MSD-319", "Invalid request input"),
	REQUEST_INVALID_SEC_LANG_ID("KER-MSD-999", "Invalid id passed for Secondary language"),
	INTERNAL_SERVER_ERROR("KER-MSD-500", "Internal server error"),
	ALREADY_ACTIVE_OR_INACTIVE("KER-MSD-998","Already activated or deactivated");

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
