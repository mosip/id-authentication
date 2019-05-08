package io.mosip.preregistration.booking.codes;

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
	PRE_REGISTRAION_ID("preRegistrationId");
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
