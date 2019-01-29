package io.mosip.registration.dto.mastersync;

/**
 * Blacklisted word DTO.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */

public class BlacklistedWordsDto extends MasterSyncBaseDto{
	/**
	 * The blacklisted word.
	 */
	private String word;
	/**
	 * The description of the word.
	 */
	private String description;
	/**
	 * The language code of the word.
	 */
	private String langCode;
	/**
	 * variable that sets the word is active or not.
	 */
	private Boolean isActive;
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
	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
