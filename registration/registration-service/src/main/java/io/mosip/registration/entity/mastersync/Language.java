package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * 
 * Entity for language
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "language", schema = "reg")
public class Language extends RegistrationCommonFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for language code
	 */
	@Id
	@Column(name = "code")
	private String languageCode;

	/**
	 * Field for language name
	 */
	@Column(name = "name")
	private String languageName;

	/**
	 * Field for language family
	 */
	@Column(name = "family")
	private String languageFamily;

	/**
	 * Field for language native name
	 */
	@Column(name = "native_name")
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
