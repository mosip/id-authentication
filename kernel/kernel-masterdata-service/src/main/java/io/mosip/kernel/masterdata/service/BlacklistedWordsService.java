package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BlackListedWordsUpdateDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.BlacklistedWordsExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
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
	 * @param langCode
	 *            language code
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
	 *            the request dto of blacklisted word to be added.
	 * @return the response.
	 */
	public WordAndLanguageCodeID createBlackListedWord(BlacklistedWordsDto blackListedWordsRequestDto);

	/**
	 * Method to update blacklisted words.
	 * 
	 * @param blackListedWordsRequestDto
	 *            the request dto of blacklisted word to be updated.
	 * @return the response.
	 */
	public WordAndLanguageCodeID updateBlackListedWord(BlackListedWordsUpdateDto blackListedWordsRequestDto);

	/**
	 * Method to delete blacklisted words.
	 * 
	 * @param blackListedWord
	 *            the word to be deleted.
	 * @return the response.
	 */

	public String deleteBlackListedWord(String blackListedWord);

	/**
	 * Method to fetch all the blacklisted words
	 * 
	 * @param pageNumber
	 *            next page number to get the requested data
	 * @param pageSize
	 *            number of data in the list
	 * @param sortBy
	 *            sorting data based the column name
	 * @param orderBy
	 *            order the list based on desc or asc
	 * 
	 * @return list of {@link BlacklistedWordsDto}
	 */
	public PageDto<BlacklistedWordsExtnDto> getBlackListedWords(int pageNumber, int pageSize, String sortBy,
			String orderBy);

	WordAndLanguageCodeID updateBlackListedWordExceptWord(BlacklistedWordsDto blacklistedWordsDto);

	public PageResponseDto<BlacklistedWordsExtnDto> searchBlackListedWords(SearchDto dto);

	public FilterResponseDto blackListedWordsFilterValues(FilterValueDto filterValueDto);

}
