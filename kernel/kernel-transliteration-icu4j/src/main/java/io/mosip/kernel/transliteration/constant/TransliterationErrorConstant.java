package io.mosip.kernel.transliteration.constant;

/**This enum contains all exception for transliteration.
 * @author Ritesh Sinha
 *@since 1.0.0
 */
/**
 * @author Ritesh Sinha
 *
 */
public enum TransliterationErrorConstant {

	TRANSLITERATION_INVALID_ID("KER-TRL-001", "Transliteration not possible"),
	TRANSLITERATION_INVALID_LANGUAGE_CODE("KER-TRL-001","Language code not supported");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 * 
	 */
	private String errorMessage;

	/**
	 * Constructor for TransliterationErrorConstant.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	TransliterationErrorConstant(String errorCode, String errorMessage) {
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
