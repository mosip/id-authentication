package io.mosip.kernel.transliteration.icu4j.constant;

/**
 * This ENUM contains language id for transliteration.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum LanguageIdConstant {

	ARABIC("Arabic"),

	FRENCH("Latin");

	/**
	 * The language id.
	 */
	private String language;

	/**
	 * Constructor for LanguageIdConstant.
	 * 
	 * @param language
	 *            the language id.
	 */
	LanguageIdConstant(String language) {
		this.language = language;
	}

	/**
	 * Getter for language id.
	 * 
	 * @return the language id.
	 */
	public String getLanguage() {
		return language;
	}

}
