package io.mosip.kernel.syncdata.constant;

public enum AdminServiceErrorCode {

	INTERNAL_SERVER_ERROR("KER-ADM-100","Internal server error"),
	REQUEST_DATA_NOT_VALID("KER-ADM-101","Request Data not valid"),
	INVALID_TIMESTAMP_EXCEPTION("KER-ADM-102","Timestamp cannot be future date"),
	LAST_UPDATED_PARSE_EXCEPTION("KER-ADM-103","Error occurred while parsing lastUpdated timestamp"),
	SYNC_JOB_DEF_FETCH_EXCEPTION("KER-ADM-104","Error while fetching sync job def details"),
	DATA_NOT_FOUND_EXCEPTION("KER-ADM-105","data not found for sync job def");
	
	private final String errorCode;
	private final String errorMessage;

	private AdminServiceErrorCode(String errorCode, String errorMessage) {
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
