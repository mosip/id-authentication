package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Language related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum LanguageErrorCode {
	NO_LANGUAGE_FOUND_EXCEPTION("KER-MSD-24", "Language not found"), LANGUAGE_FETCH_EXCEPTION("KER-MSD-23",
			"Error occured while fetching Languages"), LANGUAGE_CREATE_EXCEPTION("KER-MSD-444",
					"Error occured while creating Language"),
	LANGUAGE_REQUEST_PARAM_EXCEPTION("KER-MSD-52",
			"Request parameter invalid");

	private final String errorCode;
	private final String errorMessage;

	private LanguageErrorCode(final String errorCode, final String errorMessage) {
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
