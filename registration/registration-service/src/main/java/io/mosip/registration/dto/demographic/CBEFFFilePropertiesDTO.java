package io.mosip.registration.dto.demographic;

/**
 * This class contains the properties required for the CBEFF file.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class CBEFFFilePropertiesDTO {

	/** The format. */
	private String format;

	/** The version. */
	private String version;

	/** The value. */
	private String value;

	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 *
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
