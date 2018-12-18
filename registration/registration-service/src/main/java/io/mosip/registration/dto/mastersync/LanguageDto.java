package io.mosip.registration.dto.mastersync;

/**
 * Dto class for Language.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public class LanguageDto {

	/**
	 * Field for language code
	 */
	private String languageCode;

	/**
	 * Field for language name
	 */
	private String languageName;

	/**
	 * Field for language family
	 */
	private String languageFamily;

	/**
	 * Field for language native name
	 */
	private String nativeName;

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * @return the languageName
	 */
	public String getLanguageName() {
		return languageName;
	}

	/**
	 * @param languageName the languageName to set
	 */
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	/**
	 * @return the languageFamily
	 */
	public String getLanguageFamily() {
		return languageFamily;
	}

	/**
	 * @param languageFamily the languageFamily to set
	 */
	public void setLanguageFamily(String languageFamily) {
		this.languageFamily = languageFamily;
	}

	/**
	 * @return the nativeName
	 */
	public String getNativeName() {
		return nativeName;
	}

	/**
	 * @param nativeName the nativeName to set
	 */
	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

}
