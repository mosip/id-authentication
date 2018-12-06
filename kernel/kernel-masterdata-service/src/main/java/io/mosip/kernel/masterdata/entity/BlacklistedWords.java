package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.WordAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for blacklisted words.
 * 
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blacklisted_words", schema = "master")
@EqualsAndHashCode(callSuper = true)
@IdClass(WordAndLanguageCodeID.class)
public class BlacklistedWords extends BaseEntity implements Serializable {

	/**
	 * Serialized version ID.
	 */
	private static final long serialVersionUID = -402658536057675404L;

	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "word", column = @Column(name = "word", nullable = false, length = 128)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })
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
	@Column(name = "descr", length = 256)
	private String description;
}
