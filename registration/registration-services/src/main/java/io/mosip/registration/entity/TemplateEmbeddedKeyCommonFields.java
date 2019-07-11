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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateEmbeddedKeyCommonFields other = (TemplateEmbeddedKeyCommonFields) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (langCode == null) {
			if (other.langCode != null)
				return false;
		} else if (!langCode.equals(other.langCode))
			return false;
		return true;
	}
}
