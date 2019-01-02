/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.errorcode;

/**
 * This Enum provides the constant variables to define Error codes.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public enum ErrorCodes {
	
	/* ErrorCode for TRANSLITRATION_FAILED */
	PRG_TRL_APP_001("PRG_TRL_APP_001"),
	
	/* ErrorCode for INCORRECT_MANDATORY_FIELDS */
	PRG_TRL_APP_002("PRG_TRL_APP_002"),
	
	/* ErrorCode for PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE */
	PRG_TRL_APP_003("PRG_TRL_APP_003"),
	
	/* ErrorCode for JSON_HTTP_REQUEST_EXCEPTION */
	PRG_TRL_APP_004("PRG_TRL_APP_004"),
	
	/* ErrorCode for JSON_PARSING_FAILED */
    PRG_TRL_APP_005("PRG_TRL_APP_005"),
    
    
	PRG_TRL_APP_006("PRG_TRL_APP_006"),
	
	
	PRG_TRL_APP_007("PRG_TRL_APP_007");

	/**
	 * @param code
	 */
	private ErrorCodes(String code) {
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
