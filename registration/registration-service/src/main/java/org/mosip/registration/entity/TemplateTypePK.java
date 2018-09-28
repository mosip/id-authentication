package org.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Composite Key for TemplateType entity
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Embeddable
public class TemplateTypePK implements Serializable{

	/**
	 * Auto-Generated Serial Version Id
	 */
	private static final long serialVersionUID = -6460213371092360115L;
	private String code;
	private String lang_code;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLang_code() {
		return lang_code;
	}
	public void setLang_code(String lang_code) {
		this.lang_code = lang_code;
	}
	
}
