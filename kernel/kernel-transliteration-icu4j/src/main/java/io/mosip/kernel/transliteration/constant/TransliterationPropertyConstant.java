package io.mosip.kernel.transliteration.constant;

/**
 * This enum contains property for transliteration.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum TransliterationPropertyConstant {

	TRANSLITERATION_ID_SEPARATOR("-");

	/**
	 * The property.
	 */
	private String property;

	/**
	 * Constructor for TransliterationPropertyConstant.
	 * 
	 * @param property
	 *            the property.
	 */
	TransliterationPropertyConstant(String property) {
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
