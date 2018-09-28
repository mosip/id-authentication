package org.mosip.registration.constants;

/**
 * Enum for Registration Transaction Type Code
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegTranType {

	// TODO: Have to include other types if applicable
	CREATED("CREATED"), UPDATED("UPDATED");

	/**
	 * @param code
	 */
	private RegTranType(String code) {
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
