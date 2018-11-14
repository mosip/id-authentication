package io.mosip.kernel.masterdata.constant;

public enum LanguageErrorCode {
	NO_LANGUAGE_FOUND_EXCEPTION("KER-MSD-49", "List of Languages does not exist"), LANGUAGE_FETCH_EXCEPTION("KER-MSD-50",
			"Error occured while fetching language"), LANGUAGE_MAPPING_EXCEPTION("KER-MSD-51",
					"Error occured while mapping language");

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
