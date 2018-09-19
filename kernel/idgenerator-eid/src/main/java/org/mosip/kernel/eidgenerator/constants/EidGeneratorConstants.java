package org.mosip.kernel.eidgenerator.constants;

public enum EidGeneratorConstants {
	MOSIP_EMPTY_INPUT_ERROR_CODE("COK-IDG-EID-002", "Empty input entered"), MOSIP_INPUT_LENGTH_ERROR_CODE(
			"COK-IDG-EID-003",
			"input length is not sufficient"), MOSIP_NULL_VALUE_ERROR_CODE("COK-IDG-EID-001", "Null value entered");

	private EidGeneratorConstants(String errorCode, String errorMessage) {
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
