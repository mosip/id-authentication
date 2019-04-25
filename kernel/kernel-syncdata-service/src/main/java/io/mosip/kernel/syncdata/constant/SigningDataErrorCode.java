package io.mosip.kernel.syncdata.constant;

public enum SigningDataErrorCode {

	RESPONSE_PARSE_EXCEPTION("KER-SGN-500", "Error occured while parsing data"),
	REST_CLIENT_EXCEPTION("KER-SGN-501","Error occured while calling an encryption API");

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
