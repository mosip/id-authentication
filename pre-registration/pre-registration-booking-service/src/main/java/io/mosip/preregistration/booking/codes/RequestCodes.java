package io.mosip.preregistration.booking.codes;

public enum RequestCodes {

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
