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
	NO_BLACKLISTED_WORDS_FOUND("KER-MSD-008", "Blacklisted word not found"), BLACKLISTED_WORDS_FETCH_EXCEPTION(
			"KER-MSD-007", "Error occurred while fetching Blacklisted words");

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
