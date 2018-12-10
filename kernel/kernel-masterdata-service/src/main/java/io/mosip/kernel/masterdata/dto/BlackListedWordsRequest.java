package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * Class for blacklisted words request.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class BlackListedWordsRequest {
	/**
	 * The data for blacklisted word.
	 */
	private BlacklistedWordsDto blacklistedword;
}
