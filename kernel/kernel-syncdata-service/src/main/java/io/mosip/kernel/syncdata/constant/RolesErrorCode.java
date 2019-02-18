package io.mosip.kernel.syncdata.constant;

public enum RolesErrorCode {

	ROLES_FETCH_EXCEPTION("KER-SNC-401", "Error occured while fetching roles");
	private final String errorCode;
	private final String errorMessage;

	private RolesErrorCode(final String errorCode, final String errorMessage) {
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
