package io.mosip.kernel.jsonvalidator.constant;


/**
 * This enum provides all the constants for property source to be used.
 * 
 * @author Swati Raj
 *
 */
public enum JsonValidatorReportConstant {
	
	LEVEL("level"), 
	MESSAGE("message"),
	WARNING("warning"),
	INSTANCE("instance"),
	POINTER("pointer"),
	AT(" at "),
	ERROR("error"),
	PATH_SEPERATOR("/");

	/**
	 * The property present in Report.
	 */
	private final String property;

	/**
	 * Setter for property.
	 * 
	 * @param property
	 *            The propert to be set
	 */
	private JsonValidatorReportConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for property.
	 * 
	 * @return The property.
	 */
	public String getProperty() {
		return property;
	}

}





