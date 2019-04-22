package io.mosip.kernel.idgenerator.tokenid.constant;

/**
 * This enum contains all TOKENID properties that are required.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum TokenIdPropertyConstant {
	ZERO_TO_NINE("1234567890"), ENCRYPTION_ALGORITHM("AES"), RANDOM_NUMBER_SIZE("32");

	/**
	 * The property.
	 */
	private String property;

	/**
	 * Constructor for PridPropertyConstant.
	 * 
	 * @param property the property.
	 */
	TokenIdPropertyConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for property.
	 * 
	 * @return the property.
	 */
	public String getProperty() {
		return property;
	}
}
