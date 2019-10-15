/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.code;

/**
 * 
 * This Enum provides the constant variables to accept input request.
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public enum RequestCodes {

	/* user id */
	USER_ID("userId"),

	/* preRegistration Id */
	PRE_REGISTRAION_ID("preRegistrationId"),

	/* status Code */
	STATUS_CODE("statusCode"),

	/* identity details */
	IDENTITY("identity"),

	/* Full name */
	FULLNAME("fullName"),

	/* postalCode */
	POSTAL_CODE("postalCode"),

	/* POA */
	POA("POA");

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
