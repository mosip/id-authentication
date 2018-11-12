package io.mosip.registration.constants;

/**
 * Enum for Registration Client Status Code
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegistrationClientStatusCode {

	CREATED("R"), 
	CORRECTION("C"), 
	UIN_UPDATE("U"), 
	UIN_LOST("L"),
	UPLOADED_SUCCESSFULLY("P"), 
	UPLOAD_SUCCESS_STATUS("S"),
	UPLOAD_ERROR_STATUS("E"),
	META_INFO_SYN_SERVER("S"),
	DELETED("D"),
	APPROVED("A"), 
	REJECTED("I"), 
	ON_HOLD("H"), 
	SERVER_VALIDATED("V"), 
	PACKET_ERROR("E"); 

	/**
	 * @param code
	 */
	private RegistrationClientStatusCode(String code) {
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
