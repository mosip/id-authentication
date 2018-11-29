package io.mosip.kernel.synchandler.constant;

public enum MasterDataErrorCode {
	APPLICATION_FETCH_EXCEPTION("KER-SYNC-001", "Error occurred while fetching Applications");

	private final String errorCode;
	private final String errorMessage;

	private MasterDataErrorCode(String errorCode, String errorMessage) {
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
