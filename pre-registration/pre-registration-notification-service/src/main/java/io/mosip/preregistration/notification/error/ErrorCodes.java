package io.mosip.preregistration.notification.error;

/**
 * @author Sanober Noor
 * @since 1.0.0
 */
public enum ErrorCodes {

	/**
	 * MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED
	 */
	PRG_PAM_ACK_001("PRG_PAM_ACK_001"),
	/**
	 * INCORRECT_MANDATORY_FIELDS
	 */
	PRG_PAM_ACK_002("PRG_PAM_ACK_002"),
	/**
	 * JSON_HTTP_REQUEST_EXCEPTION
	 */
	PRG_PAM_ACK_003("PRG_PAM_ACK_003"),
	/**
	 * JSON_PARSING_FAILED
	 */
	PRG_PAM_ACK_004("PRG_PAM_ACK_004"),
	/**
	 * INPUT_OUTPUT_EXCEPTION
	 */
	PRG_PAM_ACK_005("PRG_PAM_ACK_005"),

	/**
	 * EMAIL_VALIDATION_EXCEPTION
	 */
	PRG_PAM_ACK_006("PRG_PAM_ACK_006"),
	/**
	 * PHONE_VALIDATION_EXCEPTION
	 */
	PRG_PAM_ACK_007("PRG_PAM_ACK_007"),
	/**
	 * FULL_NAME_VALIDATION_EXCEPTION
	 */
	PRG_PAM_ACK_008("PRG_PAM_ACK_008"),
	/**
	 * APPOINTMENT_DATE_NOT_CORRECT
	 */
	PRG_PAM_ACK_009("PRG_PAM_ACK_009"),

	/**
	 * APPOINTMENT_TIME_NOT_CORRECT
	 */
	PRG_PAM_ACK_010("PRG_PAM_ACK_010"),
	/**
	 * DEMOGRAPHIC_CALL_FAILED
	 * 
	 */
	PRG_PAM_ACK_011("PRG_PAM_ACK_011"),
	
	/**
	 * BOOKING_CALL_FAILED
	 * 
	 */
	PRG_PAM_ACK_012("PRG_PAM_ACK_012");
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
