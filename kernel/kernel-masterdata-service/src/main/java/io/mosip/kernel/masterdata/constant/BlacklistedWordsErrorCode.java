package io.mosip.kernel.masterdata.constant;

/**
 * ENUM constants for blacklisted words.
 * 
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public enum BlacklistedWordsErrorCode {
	NO_BLACKLISTED_WORDS_FOUND("KER-MSD-008", "Blacklisted word not found"), 
	BLACKLISTED_WORDS_FETCH_EXCEPTION("KER-MSD-007", "Error occurred while fetching Blacklisted words"), 
	BLACKLISTED_WORDS_INSERT_EXCEPTION("KER-MSD-009", "Error occurred while inserting Blacklisted words");
	/**
	 * The error code.
	 */
	private final String errorCode;
	/**
	 * The error message.
	 */
	private final String errorMessage;

	/**
	 * Constructor for BlacklistedWordsErrorCode.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	private BlacklistedWordsErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
