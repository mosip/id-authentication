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
	TRANSLITRATION_FAILED("Failed to transliterate"),
	/**
	 * ErrorMessage for PRG_TRL_APP_002
	 */
	INCORRECT_MANDATORY_FIELDS("Incorrect mandatory Fields"),
	/**
	 * ErrorMessage for PRG_TRL_APP_003
	 */
	PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE("PreReg transliteration table is not accessible"),
	/**
	 * ErrorMessage for PRG_TRL_APP_004
	 */
	JSON_HTTP_REQUEST_EXCEPTION("Invalid Json request"),
	/**
	 * ErrorMessage for PRG_TRL_APP_005
	 */
	JSON_PARSING_FAILED("Json parsing failed"),
	
	UNSUPPORTED_LANGUAGE("Unsupported language");
	/**
	 * @param code
	 */
	private ErrorMessage(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
}
