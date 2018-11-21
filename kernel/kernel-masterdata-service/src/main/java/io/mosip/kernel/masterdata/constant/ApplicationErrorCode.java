package io.mosip.kernel.masterdata.constant;

public enum ApplicationErrorCode {
	APPLICATION_FETCH_EXCEPTION("KER-APP-001", "Error ocurred while fetching application details."),
	APPLICATION_INSERT_EXCEPTION("KER-APP-444", "Error ocurred while inserting application details."),
	APPLICATION_MAPPING_EXCEPTION("KER-APP-002", "Error occured while mapping application details."),
	APPLICATION_NOT_FOUND_EXCEPTION("KER-APP-003", "No application found.");
	
	private final String errorCode;
	private final String errorMessage;
	
	private ApplicationErrorCode(String errorCode, String errorMessage) {
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
