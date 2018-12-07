package io.mosip.registration.dto.mastersync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for fetching gender data
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenderTypeDto {

	private String genderCode;
	private String genderName;
	private String langCode;

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

	/**
	 * @return the genderName
	 */
	public String getGenderName() {
		return genderName;
	}

	/**
	 * @param genderName the genderName to set
	 */
	public void setGenderName(String genderName) {
		this.genderName = genderName;
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
