package io.mosip.registration.dto.mastersync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * blacklisted word Dto
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlacklistedWordsDto {
	private String word;
	private String description;
	private String langCode;

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
