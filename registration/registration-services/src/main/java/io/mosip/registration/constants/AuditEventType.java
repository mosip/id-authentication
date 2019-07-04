package io.mosip.registration.constants;

/**
 * The {@link Enum} for the types of Audit Events
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AuditEventType {


	USER_EVENT("USER"),
	SYSTEM_EVENT("SYSTEM");

	/**
	 * @param code
	 */
	private AuditEventType(String code) {
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
