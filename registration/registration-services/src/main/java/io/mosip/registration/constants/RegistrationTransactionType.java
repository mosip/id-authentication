package io.mosip.registration.constants;

/**
 * Enum for Registration Transaction Type Code
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegistrationTransactionType {

	CREATED("CREATED"), UPDATED("UPDATED"), EXPORTED("EXPORTED");

	/**
	 * @param code
	 */
	private RegistrationTransactionType(String code) {
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
