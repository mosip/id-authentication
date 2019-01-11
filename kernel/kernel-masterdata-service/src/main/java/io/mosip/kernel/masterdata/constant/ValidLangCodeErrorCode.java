package io.mosip.kernel.masterdata.constant;

/**
 * Constants for valid language code
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public enum ValidLangCodeErrorCode {

	LANG_CODE_VALIDATION_EXCEPTION("KER-MSD-XXX", "Error occured while validating Language Code");

	private String errorCode;
	private String errorMessage;

	private ValidLangCodeErrorCode(String errorCode, String errorMessage) {
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