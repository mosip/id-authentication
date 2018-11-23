package io.mosip.kernel.auditmanager.constant;

/**
 * Constants for Audit Manager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum AuditErrorCode {
	HANDLEREXCEPTION("KER-AUD-001", "Invalid Audit Request. Required parameters must be present"),

	INVALIDFORMAT("KER-AUD-002", "ActionTimeStamp should be in ISO 8601 format (e.g. 2019-04-05T14:30)");

	private final String errorCode;
	private final String errorMessage;

	private AuditErrorCode(final String errorCode, final String errorMessage) {
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
