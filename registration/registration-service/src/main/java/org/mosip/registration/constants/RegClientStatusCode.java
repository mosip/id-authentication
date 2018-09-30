package org.mosip.registration.constants;

/**
 * Enum for Registration Client Status Code
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegClientStatusCode {

	CREATED("R"), 
	CORRECTION("C"), 
	UIN_UPDATE("U"), 
	UIN_LOST("L"),
	UPLOADED_SUCCESSFULLY("P"), 
	META_INFO_SYN_SERVER("S"), 
	DELETED("D"),
	APPROVED("A"), 
	REJECTED("I"), 
	ON_HOLD("H"), 
	PACKET_ERROR("E");

	/**
	 * @param code
	 */
	private RegClientStatusCode(String code) {
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
