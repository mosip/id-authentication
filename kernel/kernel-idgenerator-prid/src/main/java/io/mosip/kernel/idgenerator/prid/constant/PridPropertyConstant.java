package io.mosip.kernel.idgenerator.prid.constant;

/**
 * This enum contains all PRID properties that are required.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum PridPropertyConstant {
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
	PridPropertyConstant(String property) {
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
