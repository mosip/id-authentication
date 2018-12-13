package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Blacklisted word DTO.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedWordsDto extends BaseDto{
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
