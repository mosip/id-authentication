package io.mosip.authentication.core.constant;

/**
 * @author Manoj SP
 *
 */
public enum DomainType {

	AUTH("Auth"),
	JWT_DATA("Device"),
	DIGITAL_ID("FTM"),;

	private final String type;
	
	private DomainType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}