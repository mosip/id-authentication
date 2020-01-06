package io.mosip.kernel.idgenerator.partnerid.constant;

/**
 * Property constant for PartnerId generator.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
public enum PartnerIdPropertyConstant {

	ID_BASE("10");

	/**
	 * The property of partnerId generator.
	 */
	private String property;

	/**
	 * Getter for property.
	 * 
	 * @return the property.
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Constructor for PartnerIdPropertyConstant.
	 * 
	 * @param property
	 *            the property.
	 */
	PartnerIdPropertyConstant(String property) {
		this.property = property;
	}

}
