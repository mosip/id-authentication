package io.mosip.kernel.masterdata.constant;

public enum HolidayErrorCode {
	
	HOLIDAY_FETCH_EXCEPTION("KER-MSD-019","Error occured while fetching Holidays"), 
	HOLIDAY_NOTFOUND("KER-MSD-020","Holiday not found"),
	HOLIDAY_INSERT_EXCEPTION("KER-MSD-065","Error Occured while inserting holiday"),
	HOLIDAY_UPDATE_EXCEPTION("KER-MSD-099","Error occurred while updating Holiday"),
	HOLIDAY_DELETE_EXCEPTION("KER-MSD-100","Error occurred while deleting Holiday");

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
