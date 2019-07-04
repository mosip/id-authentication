package io.mosip.registration.dto.demographic;

/**
 * This class will contains the language code and value for the field
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class ValuesDTO {

	/** The language. */
	private String language;

	/** The value. */
	private String value;

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
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
