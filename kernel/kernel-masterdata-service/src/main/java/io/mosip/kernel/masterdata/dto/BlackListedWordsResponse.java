package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * DTO class for blacklisted words response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Data
public class BlackListedWordsResponse {
	/**
	 * The blacklisted word added.
	 */
	private String word;
	/**
	 * The language code of the word added.
	 */
	private String langCode;

}
