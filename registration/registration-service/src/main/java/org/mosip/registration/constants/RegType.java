package org.mosip.registration.constants;

/**
 * Enum for Registration Type Code
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegType {

	NEW("N"), CORRECTION("C"), UIN_UPDATE("U"), UIN_LOST("L"),
	ACTIVATE_UIN("A"), DEACTIVATE_UIN("D"), SUPERVISOR_HOLD("O"),
	SUPERVISOR_REJECTED("R");

	/**
	 * @param code
	 */
	private RegType(String code) {
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
