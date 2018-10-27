package io.mosip.kernel.ridgenerator.constant;

public enum RidGeneratorConstants {
	MOSIP_EMPTY_INPUT_ERROR_CODE("KER-RIG-002", "Empty input entered"), MOSIP_INPUT_LENGTH_ERROR_CODE(
			"KER-RIG-003",
			"input length is not sufficient"), MOSIP_NULL_VALUE_ERROR_CODE("KER-RIG-001", "Null value entered");

	private RidGeneratorConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public final String errorCode;
	public final String errorMessage;

}
