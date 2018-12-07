package io.mosip.kernel.masterdata.constant;

public enum HolidayErrorCode {
	HOLIDAY_FETCH_EXCEPTION("KER-MSD-019",
			"Error occured while fetching Holidays"), ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION("KER-MSD-020",
					"Holiday not found"), HOLIDAY_INSERT_EXCEPTION("111","XXX");

	private final String errorCode;
	private final String errorMessage;

	private HolidayErrorCode(final String errorCode, final String errorMessage) {
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
