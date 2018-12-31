/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.errorcode;

/**
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public enum ErrorMessage {
	
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	TRANSLITRATION_FAILED("TRANSLITRATION_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_002
	 */
	INCORRECT_MANDATORY_FIELDS("INCORRECT_MANDATORY_FIELDS"),
	/**
	 * ErrorMessage for PRG_TRL_APP_003
	 */
	PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE("PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE"),
	/**
	 * ErrorMessage for PRG_TRL_APP_004
	 */
	JSON_HTTP_REQUEST_EXCEPTION("JSON_HTTP_REQUEST_EXCEPTION"),
	/**
	 * ErrorMessage for PRG_TRL_APP_005
	 */
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
