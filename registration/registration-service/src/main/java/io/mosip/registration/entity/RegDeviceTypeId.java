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
	private String languageCode;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode
	 *            the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
