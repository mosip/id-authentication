package io.mosip.authentication.core.constant;

/**
 * Defined request type for any type of request in IDA.
 * 
 * @author Rakesh Roshan
 */
public enum RequestType {

	OTP_REQUEST("OTP-REQUEST"), OTP_AUTH("OTP-AUTH"), DEMO_AUTH("DEMO-AUTH"),FINGER_AUTH("FINGER-AUTH"), IRIS_AUTH("IRIS-AUTH"), FACE_AUTH("FACE-AUTH");

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
