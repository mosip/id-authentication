package io.mosip.kernel.masterdata.constant;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum UserDetailsErrorCode {
	INVALID_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION("",
			"INVALID_EFFECTIVE_DATE_TIME_FORMATE"), USER_HISTORY_FETCH_EXCEPTION("",
					"USER_HISTORY_FETCH_EXCEPTION"), USER_HISTORY_NOT_FOUND_EXCEPTION("",
							"USER_HISTORY_NOT_FOUND_EXCEPTION");

	private final String errorCode;
	private final String errorMessage;

	private UserDetailsErrorCode(final String errorCode, final String errorMessage) {
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
