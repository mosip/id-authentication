package io.mosip.registration.processor.core.constant;

/**
 * The Enum RegistrationType for different types of Registration.
 * 
 * @author Pranav Kumar
 * @since 0.10.2
 *
 */
public enum RegistrationType {

	/** The new. */
	NEW("new"),

	/** The update. */
	UPDATE("update"),
	
	/** The res update. */
	RES_UPDATE("res_update"),

	/** The correction. */
	CORRECTION("correction"),

	/** The activated. */
	ACTIVATED("activated"),

	/** The deactivated. */
	DEACTIVATED("deactivated"),

	/** The lost*/
	LOST("lost");
	
	public String regType;
	
	private RegistrationType(String regType) {
		this.regType=regType;
	}
	
	@Override
	public String toString() {
		return regType;
	}
}
