package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.WordAndLanguageCodeID;

/**
 * Entity class for blacklisted words.
 * 
 * @author Sreekar Chukka
 * @author Sagar Mahapatra
 * @since 1.0.0
 */

@Entity
@Table(name = "blacklisted_words", schema = "reg")
@IdClass(WordAndLanguageCodeID.class)
public class MasterBlacklistedWords extends MasterSyncBaseEntity implements Serializable {

	/**
	 * Serialized version ID.
	 */
	private static final long serialVersionUID = -402658536057675404L;

	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "word", column = @Column(name = "word")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })
	/**
	 * The blacklisted word.
	 */
	private String word;

	/**
	 * The language code of the word.
	 */
	private String langCode;

	/**
	 * The description of the word.
	 */
	@Column(name = "descr")
	private String description;

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
	
	
}
