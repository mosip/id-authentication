package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.RegDeviceType;
import lombok.Data;

/**
 * composite primary key of {@link RegDeviceType}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 */

@Embeddable
@Data
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
