package io.mosip.resident.constant;

public enum ResidentErrorCode {

	NO_RID_FOUND_EXCEPTION("RES-SER-001", "RID not found"), INVALID_REQUEST_EXCEPTION("RES-SER-002",
			"One or more input parameter is invalid or does not exist"), TOKEN_GENERATION_FAILED("RES-SER-003",
					"Token generation failed"), OTP_VALIDATION_FAILED("RES-SER-004",
							"OTP validation failed"), API_RESOURCE_UNAVAILABLE("RES-SER-005",
									"API resource is not available"), VID_CREATION_EXCEPTION("RES-SER-006",
											"Exception while creating vid"), VID_ALREADY_PRESENT("RES-SER-007",
													"Maximum allowed VIDs are active. Deactivate VID to generate new one."), INVALID_INPUT(
															"RES-SER-008",
															"Invalid Input - "), IN_VALID_UIN_OR_RID("RES-SER-009",
																	"In Valid UIN or RID"), IN_VALID_UIN_OR_VID(
																			"RES-SER-010",
																			"Data entered is not valid."), AUTH_TYPE_STATUS_UPDATE_FAILED(
																					"RES-SER-011",
																					"Your request is not successful, please try again later.");
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
