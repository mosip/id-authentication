/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.notification.code;

/**
 * 
 * This Enum provides the constant variables to accept input request.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */

public enum RequestCodes {

	/* preRegistration Id */
	PREID(""),
   /**
 * 
 */
SMS("sms"),  
/**
 * 
 */
EMAIL("email"),
MESSAGE("Email and sms request successfully submitted");
	
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
