package org.mosip.auth.core.constant;

/**
 * Defined request type as 'otp' or 'auth'.
 * 
 * @author Rakesh Roshan
 */
public enum RequestType {

	OTP_TRIGGER("otp"), OTP_AUTH("auth");

	String requestType;

	/**
	 * Initialize RequestType enum with requestType value.
	 * 
	 * @param requestType
	 */
	RequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * Get request type.
	 * 
	 * @return requestType
	 */
	String getRequestType() {
		return requestType;
	}

}
