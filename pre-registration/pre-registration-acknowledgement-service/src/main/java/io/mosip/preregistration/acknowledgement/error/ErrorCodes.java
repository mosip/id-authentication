package io.mosip.preregistration.acknowledgement.error;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
public enum ErrorCodes {
	
	/**
	 * MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED
	 */
	PRG_ACK_001("PRG_ACK_001"),
	/**
	 * INCORRECT_MANDATORY_FIELDS
	 */
	PRG_ACK_002("PRG_ACK_002"),
	/**
	 * JSON_HTTP_REQUEST_EXCEPTION
	 */
	PRG_ACK_003("PRG_ACK_003"),
	/**
	 * JSON_PARSING_FAILED
	 */
	PRG_ACK_004("PRG_ACK_004"),
	/**
	 * INPUT_OUTPUT_EXCEPTION
	 */
	PRG_ACK_005("PRG_ACK_005"); 


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
