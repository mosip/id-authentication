package io.mosip.resident.constant;

public enum ResidentErrorCode {

	NO_RID_FOUND_EXCEPTION("RES-SER-001", "RID not found"),
	INVALID_REQUEST_EXCEPTION("RES-SER-002","One or more input parameter is invalid or does not exist"),
	TOKEN_GENERATION_FAILED("RES-SER-003","Token generation failed"),
	OTP_VALIDATION_FAILED("RES-SER-004","OTP validation failed"),
	API_RESOURCE_UNAVAILABLE("RES-SER-005","API resource is not available"),
	API_RESOURCE_ACCESS_EXCEPTION("RES-SER-006","unable to access API resource"),
	VID_CREATION_EXCEPTION("RES-SER-007", "Exception while creating vid"), 
	VID_ALREADY_PRESENT("RES-SER-008","Maximum allowed VIDs are active. Deactivate VID to generate new one."), 
	INVALID_INPUT("RES-SER-009","Invalid Input - "),
	IN_VALID_VID("RES-SER-0010", "In Valid VID"),
	IN_VALID_UIN("RES-SER-011", "In Valid UIN"),
	IN_VALID_RID("RES-SER-012", "In Valid RID"),
	IN_VALID_VID_UIN("RES-SER-013", "In Valid UIN for given VID"),
	REQUEST_FAILED("RES-SER-014", "Your request is not successful, please try again later."),
	TEMPLATE_EXCEPTION("RES-SER-015","Template exception"),
	NOTIFICATION_FAILURE("RES-SER-016","Sending notification(Email and SMS) to resident failed"),
	IN_VALID_UIN_OR_VID("RES-SER-013", "In Valid UIN or VID"),
	//system exceptions
	RESIDENT_SYS_EXCEPTION("RES-SER-SYS-001","System exception occured"),
	IO_EXCEPTION("RES-SER-SYS-002","IO Exception occured"),
	JSON_PROCESSING_EXCEPTION("RES-SER-SYS-003","JSON Processing Exception occured");
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
