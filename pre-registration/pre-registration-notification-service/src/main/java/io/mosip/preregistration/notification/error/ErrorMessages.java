package io.mosip.preregistration.notification.error;

/**
 * @author Sanober Noor
 * @since 1.0.0
 */
public enum ErrorMessages {

	/**
	 * ErrorMessage for PRG_ACK_001
	 */
	MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED("Mobile number or Email Id is missing"),
	/**
	 * ErrorMessage for PRG_ACK_002
	 */
	INCORRECT_MANDATORY_FIELDS("Mandatory fields are missing"),
	/**
	 * ErrorMessage for PRG_ACK_003
	 */
	JSON_HTTP_REQUEST_EXCEPTION("Json http request exception"),

	/**
	 * ErrorMessage for PRG_ACK_004
	 */
	JSON_PARSING_FAILED("Json is not able to parse"),
	/**
	 * @param code
	 *            ErrorMessage for PRG_ACK_005
	 */

	INPUT_OUTPUT_EXCEPTION("INPUT_OUTPUT_EXCEPTION"),

	/**
	 * ErrorMessage for PRG_PAM_ACK_006
	 */

	EMAIL_VALIDATION_EXCEPTION("Email is not valid"),

	/**
	 * ErrorMessage for PRG_PAM_ACK_007
	 */
	PHONE_VALIDATION_EXCEPTION("Phone number is not valid"),
	/**
	 * ErrorMessage for PRG_PAM_ACK_008
	 */
	FULL_NAME_VALIDATION_EXCEPTION("Full name is not valid"),

	/**
	 * ErrorMessage for PRG_PAM_ACK_009
	 */
	APPOINTMENT_DATE_NOT_CORRECT("Appointment date is not valid"),
	/**
	 * ErrorMessage for PRG_PAM_ACK_010
	 */
	APPOINTMENT_TIME_NOT_CORRECT("Appointment time is not valid"),
	
	/**
	 * ErrorMessage for PRG_PAM_ACK_011
	 */
	DEMOGRAPHIC_CALL_FAILED("Demographic rest call failed"),
	
	/**
	 * ErrorMessage for PRG_PAM_ACK_012
	 * 
	 */
	BOOKING_CALL_FAILED("Booking rest call failed");
	
	private ErrorMessages(String code) {
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
