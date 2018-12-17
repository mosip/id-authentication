package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.entity.id.WordAndLanguageCodeID;

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
	 * @param langCode language code
	 * @return {@link BlacklistedWordsResponseDto}
	 */
	BlacklistedWordsResponseDto getAllBlacklistedWordsBylangCode(String langCode);

	/**
	 * @param blacklistedwords
	 *            the words to validate
	 * @return if the words are valid or invaid
	 */
	public boolean validateWord(List<String> blacklistedwords);

	/**
	 * Method to add blacklisted words.
	 * 
	 * @param blackListedWordsRequestDto
	 *            the request dto of blackliisted word to be added.
	 * @return the response.
	 */
	public WordAndLanguageCodeID createBlackListedWord(RequestDto<BlacklistedWordsDto> blackListedWordsRequestDto);
}
