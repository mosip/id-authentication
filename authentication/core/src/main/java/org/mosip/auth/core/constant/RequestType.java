package org.mosip.auth.core.constant;

/**
 * Defined request type as 'otp' or 'auth'.
 * 
 * @author Rakesh Roshan
 */
public enum RequestType {

	OTP_REQUEST("OTP-REQUEST"), OTP_AUTH("OTP-AUTH"), DEMO_AUTH("DEMO-AUTH"), BIO_AUTH("BIO-AUTH");

	String type;

	/**
	 * Initialize RequestType enum with requestType value.
	 * 
	 * @param requestType
	 */
	RequestType(String type) {
		this.type = type;
	}

	/**
	 * Get request type.
	 * 
	 * @return requestType
	 */
	public String getRequestType() {
		return type;
	}

}
