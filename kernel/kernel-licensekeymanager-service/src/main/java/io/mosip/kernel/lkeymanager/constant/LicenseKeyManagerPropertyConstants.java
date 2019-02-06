package io.mosip.kernel.lkeymanager.constant;

/**
 * ENUM to manage constant values defined in the service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum LicenseKeyManagerPropertyConstants {
	TIME_ZONE("UTC"), 
	DEFAULT_CREATED_BY("defaultadmin@mosip.io"),
	MAPPED_STATUS("Mapped License with the permissions");

	/**
	 * The value.
	 */
	private String value;

	/**
	 * Constructor with value as the argument.
	 * 
	 * @param value
	 *            the value.
	 */
	private LicenseKeyManagerPropertyConstants(String value) {
		this.value = value;
	}

	/**
	 * Getter for value.
	 * 
	 * @return the value.
	 */
	public String getValue() {
		return this.value;
	}
}
