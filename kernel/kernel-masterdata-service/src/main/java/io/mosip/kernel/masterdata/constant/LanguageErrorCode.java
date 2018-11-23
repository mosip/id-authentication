package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Language related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum LanguageErrorCode {
	NO_LANGUAGE_FOUND_EXCEPTION("KER-MSD-024", "List of Languages does not exist"), LANGUAGE_FETCH_EXCEPTION(
			"KER-MSD-023", "Error occured while fetching language"), LANGUAGE_REQUEST_PARAM_EXCEPTION("KER-MSD-XX",
					"Request parameter invalid"), LANGUAGE_CREATE_EXCEPTION("KER-MSD-XX",
							"Error occured while creating Language");

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
