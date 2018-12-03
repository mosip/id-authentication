package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * Blacklisted word DTO.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data


public class BlacklistedWordsDto {
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
}
