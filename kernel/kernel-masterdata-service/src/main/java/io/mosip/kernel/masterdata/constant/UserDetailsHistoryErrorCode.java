package io.mosip.kernel.masterdata.constant;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum UserDetailsHistoryErrorCode {
	INVALID_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION("KER-USR-002",
			"Invalid Date Format"), USER_HISTORY_FETCH_EXCEPTION("KER-USR-001",
					"Error occurred while retrieving User History"), USER_HISTORY_NOT_FOUND_EXCEPTION("KER-USR-003",
							"User History not found");

	private final String errorCode;
	private final String errorMessage;

	private UserDetailsHistoryErrorCode(final String errorCode, final String errorMessage) {
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
