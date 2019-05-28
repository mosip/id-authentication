package io.mosip.admin.uinmgmt.constant;

public enum UinGenerationStatusErrorCode {
	UIN_GENERATION_STATUS_EXCEPTION("XX","Error while fetching uin generation status"), PARSE_EXCEPTION("XX","error while parsing ");

	
	
	
	private final String errorCode;
	private final String errorMessage;

	private UinGenerationStatusErrorCode(String errorCode, String errorMessage) {
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
