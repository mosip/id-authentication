package io.mosip.kernel.masterdata.constant;

public enum WorkingNonWorkingDayErrorCode {
	
	INVALID_REG_CENTER_ID("KER-WKDS-001","Registration center ID is not valid"),
	WORKING_DAY_TABLE_NOT_ACCESSIBLE("KER-WKDS-002","reg_working_nonworking table not accessible"),
	WEEK_DAY_DATA_FOUND_EXCEPTION("KER-WKDS-003","No week day found"),
	WORKING_DAY_DATA_FOUND_EXCEPTION("KER-WKDS-004","No working/non working day data found");
	
	private final String errorCode;
	private final String errorMessage;

	private WorkingNonWorkingDayErrorCode(final String errorCode, final String errorMessage) {
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
