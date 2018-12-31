/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.code;

/**
 * 
 * This Enum provides the constant variables to accept input request.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public enum RequestCodes {

	/* id */
	ID("id"), 

	/* version */
	VER("ver"), 

	/* request date time */
	REQ_TIME("reqTime"), 

	/* request object */
	REQUEST("request");
	
	/**
	 * @param code
	 */
	private RequestCodes(String code) {
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
