package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite Key for TemplateType entity
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */

@Embeddable
public class TemplateEmbeddedKeyCommonFields implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String code;
	@Column(name="lang_code")
	protected String langCode;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
}
