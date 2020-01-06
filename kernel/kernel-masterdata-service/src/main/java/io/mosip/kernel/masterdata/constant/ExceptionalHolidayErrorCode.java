package io.mosip.kernel.masterdata.constant;

public enum ExceptionalHolidayErrorCode {
	
	EXCEPTIONAL_HOLIDAY_FETCH_EXCEPTION("KER-EHD-001", "Error occured while fetching Exceptional Holidays"),
	EXCEPTIONAL_HOLIDAY_NOTFOUND("KER-EHD-002", "Exceptional Holiday not found");

	private final String errorCode;
	private final String errorMessage;

	private ExceptionalHolidayErrorCode(final String errorCode, final String errorMessage) {
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
