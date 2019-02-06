package io.mosip.preregistration.core.errorcodes;

/**
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Kishan Rathore
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
public enum ErrorMessages {
	INVALID_REQUEST_USER_ID("INVALID_REQUEST_USER_ID"),
	INVALID_REQUEST_ID("INVALID_REQUEST_ID"), // PRG_CORE_REQ_001
	INVALID_REQUEST_VERSION("INVALID_REQUEST_VERSION"), //  PRG_CORE_REQ_002
	INVALID_REQUEST_DATETIME("INVALID_REQUEST_DATETIME"), //  PRG_CORE_REQ_003
	INVALID_REQUEST_BODY("INVALID_REQUEST_BODY"),// PRG_CORE_REQ_004
	INVALID_PRE_REGISTRATION_ID("INVALID_PRE_REGISTRATION_ID"),
	INVALID_STATUS_CODE("INVALID_STATUS_CODE"),
	INVALID_DATE("INVALID_DATE"), 
	APPOINTMENT_CANNOT_BE_BOOKED("APPOINTMENT_CANNOT_BE_BOOKED"),
	APPONIMENT_CANNOT_BE_CANCELED("APPONIMENT_CANNOT_BE_CANCELED"), 
	APPONIMENT_CANNOT_BE_REBOOK("APPONIMENT_CANNOT_BE_REBOOK"),
	FAILED_TO_ENCRYPT("FAILED_TO_ENCRYPT"),
	FAILED_TO_DECRYPT("FAILED_TO_DECRYPT")
	;
	
	private ErrorMessages(String message) {
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
