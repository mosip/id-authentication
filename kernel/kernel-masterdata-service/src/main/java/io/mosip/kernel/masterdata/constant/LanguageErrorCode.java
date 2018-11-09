package io.mosip.kernel.masterdata.constant;

public enum LanguageErrorCode {
	NO_LANGUAGE_FOUND_EXCEPTION("KER-MSD-XXX", "No Language found"), LANGUAGE_FETCH_EXCEPTION("KER-MSD-XXX",
					"Error occured while fetching language");

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
