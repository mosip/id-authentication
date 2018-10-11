package io.mosip.kernel.idgenerator.tokenid.constant;

public enum TokenIdGeneratorErrorCodes {
	UNABLE_TO_CONNECT_TO_DB("KER-PRD-001", "Unable to connect to the database");
	private final String errorCode;
	private final String errorMessage;
	
	
	private TokenIdGeneratorErrorCodes(final String errorCode, final String errorMessage) {
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
