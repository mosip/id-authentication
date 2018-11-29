package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * DTO class for BlackListedWords.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class BlackListedWordsRequestDto {
	/**
	 * The id.
	 */
	private String id;
	/**
	 * The version.
	 */
	private String ver;
	/**
	 * The timestamp.
	 */
	private String timestamp;
	/**
	 * The request.
	 */
	private BlackListedWordsRequest request;
}
