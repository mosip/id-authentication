package io.mosip.kernel.responsesignature.constant;

public enum SigningDataErrorCode {

	RESPONSE_PARSE_EXCEPTION("KER-SGN-100", "Error occured while parsing data"),
	REST_CLIENT_EXCEPTION("KER-SGN-101","Error occured while calling an API");

	private final String errorCode;
	private final String errorMessage;

	private SigningDataErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
