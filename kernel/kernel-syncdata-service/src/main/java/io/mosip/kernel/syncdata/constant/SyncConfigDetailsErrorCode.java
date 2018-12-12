package io.mosip.kernel.syncdata.constant;

public enum SyncConfigDetailsErrorCode {

	SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION("KER-SYNC-127","Error occured in service"),
	SYNC_CONFIG_DETIAL_INPUT_PARAMETER_EXCEPTION("KER-SYNC-128","Input parameter is missing");
	
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
