package io.mosip.kernel.syncdata.constant;

public enum RegistrationCenterUserErrorCode {

	REGISTRATION_USER_FETCH_EXCEPTION("KER-SYNC-XXX", "Error occurred while fetching Registration center users"),
	
	REGISTRATION_USER_DATA_NOT_FOUND_EXCEPTION("KER-SYNC-XXX","Registration center user not found ");
	
	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterUserErrorCode(String errorCode, String errorMessage) {
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
