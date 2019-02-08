package io.mosip.registration.entity.mastersync.id;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

/**
 * ID class for the columns word and language code.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
@Data
public class WordAndLanguageCodeID implements Serializable {

	/**
	 * Generated Serialized ID.
	 */
	private static final long serialVersionUID = 2309013416400782373L;

	/**
	 * The blacklisted word.
	 */
	@Column(name = "word")
	private String word;

	/**
	 * The language code.
	 */
	@Column(name = "lang_code")
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
