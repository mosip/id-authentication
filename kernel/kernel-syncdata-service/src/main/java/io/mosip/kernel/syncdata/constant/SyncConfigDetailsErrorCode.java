package io.mosip.kernel.syncdata.constant;

public enum SyncConfigDetailsErrorCode {

	SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION("KER-SNC-001","Error occured in service"),
	SYNC_CONFIG_DETAIL_INPUT_PARAMETER_EXCEPTION("KER-SNC-002","Input parameter is missing");
	
	private final String errorCode;
	private final String errorMessage;

	private SyncConfigDetailsErrorCode(String errorCode, String errorMessage) {
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
