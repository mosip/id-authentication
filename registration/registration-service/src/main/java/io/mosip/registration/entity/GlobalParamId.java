package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * Composite key for GlobalParam entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class GlobalParamId implements Serializable{

	private static final long serialVersionUID = 4798525506099635089L;
	
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
