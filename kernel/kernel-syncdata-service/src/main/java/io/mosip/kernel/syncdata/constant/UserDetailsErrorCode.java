package io.mosip.kernel.syncdata.constant;

public enum UserDetailsErrorCode {
	
	USER_DETAILS_FETCH_EXCEPTION("KER-sync-xxx", "Error occured while fetching User Details");
	private final String errorCode;
	private final String errorMessage;

	private UserDetailsErrorCode(final String errorCode, final String errorMessage) {
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
