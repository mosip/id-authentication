package io.mosip.kernel.core.util.constant;

public enum DateUtilConstants {
	ILLEGALARGUMENT_ERROR_CODE("KER-UTL-001", "Invalid Argument Found");

	/** Error code. */
	public final String errorCode;
	/** Exception Message */
	public final String exceptionMessage;

	/**
	 * @param errorCode
	 *            source Error code to use when no localized code is available
	 * @param exceptionMessage
	 *            source exception message to use when no localized message is
	 *            available.
	 */
	DateUtilConstants(final String errorCode, final String exceptionMessage) {
		this.errorCode = errorCode;
		this.exceptionMessage = exceptionMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getEexceptionMessage() {
		return exceptionMessage;
	}
}
