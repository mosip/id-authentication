package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * composite primary key of {@link RegDeviceTypeId}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 */

@Embeddable
public class RegDeviceTypeId implements Serializable {
	private static final long serialVersionUID = -8748623866593150099L;

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

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

}
