package io.mosip.preregistration.booking.serviceimpl.codes;

public enum RequestCodes {

	/* id */
	id("id"),

	/* version */
	version("version"),

	/* request date time */
	requesttime("requesttime"),

	/* request object */
	request("request"),

	/* preRegistration Id */
	PRE_REGISTRAION_ID("preRegistrationId"),

	REG_DATE("appointment_date"), 
	
	FROM_SLOT_TIME("time_slot_from");
	/**
	 * @param code
	 */
	private RequestCodes(String code) {
		this.code = code;
	}

	/**
	 * Code
	 */
	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
