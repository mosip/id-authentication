package io.mosip.admin.masterdata.constant;

public enum MasterDataColumnErrorCode {
	PROPERTY_NOT_FOUND("XX","Property not found"), CONFIG_FILE_NOT_FOUND("XX","file not found");
	
	
	private final String errorCode;
	private final String errorMessage;

	private MasterDataColumnErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String errorCode() {
		return this.errorCode;
	}

	public String errorMessage() {
		return this.errorMessage;
	}

}
