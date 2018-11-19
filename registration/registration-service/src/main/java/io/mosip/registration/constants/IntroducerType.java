package io.mosip.registration.constants;

/**
 * The enums for introducer types
 * 
 * @author Taleev Aalam
 * @since 1.0.0
 *
 */
public enum IntroducerType {
	
	PARENT("Parent"), HOF("HOF"), INTRODUCER("Introducer");

	/**
	 * @param code
	 */
	private IntroducerType(String code) {
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
