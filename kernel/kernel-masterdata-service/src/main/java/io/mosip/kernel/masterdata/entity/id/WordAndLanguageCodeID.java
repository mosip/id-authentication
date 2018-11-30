package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ID class for the columns word and language code.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordAndLanguageCodeID implements Serializable {

	/**
	 * Generated Serialized ID.
	 */
	private static final long serialVersionUID = 2309013416400782373L;

	/**
	 * The blacklisted word.
	 */
	@Column(name = "word", nullable = false, length = 128)
	private String word;

	/**
	 * The language code.
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
}
