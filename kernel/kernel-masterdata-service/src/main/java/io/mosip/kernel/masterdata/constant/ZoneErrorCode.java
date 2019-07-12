package io.mosip.kernel.masterdata.constant;

public enum ZoneErrorCode {

	ZONE_FETCH_EXCEPTION("XXX", "Error Occured while Fetching Zone"),
	USER_ZONE_UNAVAILABLE("XXX","No zone found for user %s"),
	USER_ZONE_FETCH_EXCEPTION("XXX","Error Occured while fetching user's zone");

	private final String errorCode;
	private final String errorMessage;

	private ZoneErrorCode(final String errorCode, final String errorMessage) {
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
