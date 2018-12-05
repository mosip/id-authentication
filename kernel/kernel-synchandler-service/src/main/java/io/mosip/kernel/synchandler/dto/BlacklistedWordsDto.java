package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Blacklisted word DTO.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
