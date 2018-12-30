package io.mosip.preregistration.transliteration.errorcode;

public enum ErrorMessage {
	
	TRANSLITRATION_FAILED("TRANSLITRATION_FAILED"),
	INCORRECT_MANDATORY_FIELDS("INCORRECT_MANDATORY_FIELDS"),
	PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE("PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE"),
	JSON_HTTP_REQUEST_EXCEPTION("JSON_HTTP_REQUEST_EXCEPTION"),
	JSON_PARSING_FAILED("JSON_PARSING_FAILED");
	/**
	 * @param code
	 */
	private ErrorMessage(String code) {
		this.code = code;
	}

	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
