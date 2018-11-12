package io.mosip.kernel.masterdata.constant;

public enum HolidayErrorCode {
	HOLIDAY_INVALID_ID_EXCEPTION("KER-MSD-045", "No Holiday found for specified holiday id"), HOLIDAY_FETCH_EXCEPTION(
			"KER-MSD-046", "Error occured while fetching holidays"), HOLIDAY_MAPPING_EXCEPTION("KER-MSD-047",
					"Error occured while mapping Holiday"), ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION("KER-MSD-048",
							"No Holiday found for specified id and language code");

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
