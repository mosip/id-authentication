package io.mosip.registration.entity.mastersync.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
public class CodeAndLanguageCodeID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7001663925687776491L;

	@Column(name = "code")
	private String code;

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	
}
