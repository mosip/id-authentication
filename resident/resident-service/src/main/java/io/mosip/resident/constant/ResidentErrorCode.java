package io.mosip.resident.constant;

public enum ResidentErrorCode {

	NO_RID_FOUND_EXCEPTION("RES-XXX-XXX", "RID not found"),
	INVALID_REQUEST_EXCEPTION("RES-XXX-XXX", "One or more input parameter is invalid or does not exist"),
	TOKEN_GENERATION_FAILED("RES-XXX-XXX", "Token generation failed"),
	OTP_VALIDATION_FAILED("RES-XXX-XXX", "OTP validation failed"),
	API_RESOURCE_UNAVAILABLE("RES-XXX-XXX", "API resource is not available"),
	VID_CREATION_EXCEPTION("RES-XXX-XXX", "Exception while creating vid");

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
