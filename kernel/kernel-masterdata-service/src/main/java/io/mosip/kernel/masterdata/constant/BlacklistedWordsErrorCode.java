package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Blacklisted words
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 *
 */
public enum BlacklistedWordsErrorCode {
	BLACKLISTED_WORDS_LANG_CODE_ARG_MISSING("KER-MSD-XX1",
			"One or more input parameter is missing"), 
	NO_BLACKLISTED_WORDS_FOUND("KER-MSD-XX2",
					"List of Blacklisted words for the requested language not exist"), 
	BLACKLISTED_WORDS_MAPPING_EXCEPTION(
							"KER-MSD-XX3",
							"Error occured while mapping blacklisted words"), 
	BLACKLISTED_WORDS_FETCH_EXCEPTION(
									"KER-MSD-XX4", "exception during fatching data from db");

	private final String errorCode;
	private final String errorMessage;

	private BlacklistedWordsErrorCode(final String errorCode, final String errorMessage) {
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
