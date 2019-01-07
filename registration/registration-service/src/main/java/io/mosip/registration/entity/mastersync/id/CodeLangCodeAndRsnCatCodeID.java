package io.mosip.registration.entity.mastersync.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */

@Embeddable
public class CodeLangCodeAndRsnCatCodeID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5622889820282234362L;

	@Column(name = "rsncat_code")
	private String rsnCatCode;

	@Column(name = "code")
	private String code;

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the rsnCatCode
	 */
	public String getRsnCatCode() {
		return rsnCatCode;
	}

	/**
	 * @param rsnCatCode the rsnCatCode to set
	 */
	public void setRsnCatCode(String rsnCatCode) {
		this.rsnCatCode = rsnCatCode;
	}

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
