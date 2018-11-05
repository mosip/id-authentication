package io.mosip.kernel.masterdata.constant;

public enum LocationErrorCode {
	DATABASE_EXCEPTION("KER-MSD-017", "Error from database"),

	RECORDS_NOT_FOUND_EXCEPTION("KER-MSD-018", "Location Hierarchy does not exist");

	private String errorCode;
	private String errorMessage;

	private LocationErrorCode(final String errorCode, final String errorMessage) {
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
