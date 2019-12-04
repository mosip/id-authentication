package io.mosip.resident.constant;

public enum ResidentErrorCode {

	NO_RID_FOUND_EXCEPTION("RES-SER-001", "RID not found"),
	INVALID_REQUEST_EXCEPTION("RES-SER-002", "One or more input parameter is invalid or does not exist"),
	TOKEN_GENERATION_FAILED("RES-SER-003", "Token generation failed"),
	OTP_VALIDATION_FAILED("RES-SER-004", "OTP validation failed"),
	API_RESOURCE_UNAVAILABLE("RES-SER-005", "API resource is not available"),
	VID_CREATION_EXCEPTION("RES-SER-006", "Exception while creating vid"),
	VID_ALREADY_PRESENT("RES-SER-007", "Maximum allowed VIDs are active. Deactivate VID to generate new one."),
	INVALID_INPUT("RES-SER-008", "Invalid Input - ");

	private final String errorCode;
	private final String errorMessage;

	private ResidentErrorCode(final String errorCode, final String errorMessage) {
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
