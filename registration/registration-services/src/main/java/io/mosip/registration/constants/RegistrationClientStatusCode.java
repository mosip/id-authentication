package io.mosip.registration.constants;

/**
 * Enum for Registration Client Status Code
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegistrationClientStatusCode {

	CREATED("REGISTERED"), 
	CORRECTION("CORRECTION"), 
	UIN_UPDATE("UPDATE"), 
	UIN_LOST("LOST"),
	UPLOADED_SUCCESSFULLY("PUSHED"), 
	UPLOAD_SUCCESS_STATUS("S"),
	UPLOAD_ERROR_STATUS("E"),
	META_INFO_SYN_SERVER("SYNCED"),
	DELETED("DELETED"),
	APPROVED("APPROVED"), 
	REJECTED("REJECTED"), 
	ON_HOLD("ON_HOLD"),
	RE_REGISTER("RE_REGISTER_APPROVED"); 

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
