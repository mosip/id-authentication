package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Entity for composite primary key in gender table in DB
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Embeddable
public class GenderTypeId implements Serializable {

	private static final long serialVersionUID = -1169678225048676557L;

	@Column(name = "code")
	private String genderCode;

	@Column(name = "lang_code")
	private String langCode;

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

	/**
	 * @return the genderCode
	 */
	public String getGenderCode() {
		return genderCode;
	}

	/**
	 * @param genderCode the genderCode to set
	 */
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

}
