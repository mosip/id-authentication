package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;

/**
 * blacklisted words service
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
public interface BlacklistedWordsService {

	/**
	 * method to fetch all blacklisted words by language code
	 * 
	 * @param langCode
	 * @return {@link BlacklistedWordsResponseDto}
	 */
	BlacklistedWordsResponseDto getAllBlacklistedWordsBylangCode(String langCode);
	
	/**
	 * @param blacklistedwords
	 *            the words to validate
	 * @return if the words are valid or invaid
	 */
	public boolean validateWord(List<String> blacklistedwords);
}
