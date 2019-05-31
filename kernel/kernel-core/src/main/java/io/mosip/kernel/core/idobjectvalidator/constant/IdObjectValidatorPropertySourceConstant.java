package io.mosip.kernel.core.idobjectvalidator.constant;

/**
 * This enum provides all the constants for property source to be used.
 * 
 * @author Manoj SP
 * @author Swati Raj
 *
 */
public enum IdObjectValidatorPropertySourceConstant {
	CONFIG_SERVER("CONFIG_SERVER"), APPLICATION_CONTEXT("APPLICATION_CONTEXT"), LOCAL("LOCAL");

	/**
	 * The property Source.
	 */
	private final String propertySource;

	/**
	 * Setter for propertySource.
	 * 
	 * @param propertySource The property source to be set
	 */
	private IdObjectValidatorPropertySourceConstant(String propertySource) {
		this.propertySource = propertySource;
	}

	/**
	 * Getter for propertySource.
	 * 
	 * @return The propertySource.
	 */
	public String getPropertySource() {
		return propertySource;
	}
}
