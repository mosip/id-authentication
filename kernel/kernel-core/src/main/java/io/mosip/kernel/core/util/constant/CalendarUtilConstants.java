package io.mosip.kernel.core.util.constant;

public enum CalendarUtilConstants {

	ILLEGAL_ARGUMENT_CODE("KER-UTL-001"), ARITHMETIC_EXCEPTION_CODE("KER-UTL-002"), NULL_ARGUMENT_CODE("KER-UTL-003"),
	ILLEGAL_ARGUMENT_MESSAGE("Invalid_Argument"), YEAR_OVERFLOW_MESSAGE("Year_can't_be_greater_than_280million"),
	DATE_NULL_MESSAGE("Date_can't_be_null");

	private final String errorCode;

	private CalendarUtilConstants(String eCode) {
		errorCode = eCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
