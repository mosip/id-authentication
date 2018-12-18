package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * blacklisted word entity
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 * @since 06-11-2018
 */

@Entity
@Table(name = "blacklisted_words", schema = "reg")
public class BlacklistedWords extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = -402658536057675404L;

	@Id
	@Column(name = "word")
	private String word;

	@Column(name = "descr")
	private String description;

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
