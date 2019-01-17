package io.mosip.kernel.idgenerator.rid.constant;

/**
 * This enum provide all constants for rid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum RidGeneratorPropertyConstant {
	TIMESTAMP_FORMAT("yyyyMMddHHmmss");

	/**
	 * The property for ridgenerator.
	 */
	private String property;

	/**
	 * The constructor to set rid property.
	 * 
	 * @param property
	 *            the property to set.
	 */
	private RidGeneratorPropertyConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for rid property
	 * 
	 * @return the rid property.
	 */
	public String getProperty() {
		return property;
	}

}
