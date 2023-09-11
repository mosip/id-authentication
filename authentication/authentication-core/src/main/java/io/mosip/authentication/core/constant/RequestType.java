package io.mosip.authentication.core.constant;

/**
 * Defined request type for any type of request in IDA.
 * 
 * @author Rakesh Roshan
 */
public enum RequestType {

	OTP_REQUEST("OTP-REQUEST", "OTP Request"), 
	OTP_AUTH("OTP-AUTH","OTP Authentication"),
	DEMO_AUTH("DEMO-AUTH","Demo Authentication"),
	FINGER_AUTH("FINGERPRINT-AUTH","Finger Authentication"), 
	IRIS_AUTH("IRIS-AUTH","Iris  Authentication"), 
	FACE_AUTH("FACE-AUTH","Face Authentication"),
	STATIC_PIN_AUTH("STATIC-PIN-AUTH","Pin Authentication"),
	STATICPIN_STORE_REQUEST("STATIC-PIN-STORAGE","Static Pin Store Request"),
	EKYC_AUTH_REQUEST("EKYC-AUTH","EKYC Authentication Request"),
	KYC_AUTH_REQUEST("KYC-AUTH","KYC Authentication Request"),
	KYC_EXCHANGE_REQUEST("KYC-EXCHANGE","KYC Exchange Request"),
	IDENTITY_KEY_BINDING("IDENTITY-KEY-BINDING","Identity Key Binding Request"),
	TOKEN_REQUEST("TOKEN-REQUEST", "Token Request"),
	TOKEN_AUTH("TOKEN-AUTH","Token based Authentication"),
	VCI_EXCHANGE_REQUEST("VCI-EXCHANGE-REQUEST","VCI Exchange Request");

	String type;
	String message;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Initialize RequestType enum with requestType value.
	 * 
	 * @param requestType
	 */
	RequestType(String type,String message) {
		this.type = type;
		this.message=message;
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
