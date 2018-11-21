package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;

/**
 * controller with api's to fetch for blacklisted words
 * 
 * @author Abhishek Kumar
 * @since 06-11-2018
 * @version 1.0.0
 */
@RestController
public class BlacklistedWordsController {
	@Autowired
	private BlacklistedWordsService blacklistedWordsService;

	/**
	 * fetch the list of blacklisted words based on language code
	 * 
	 * @param langCode
	 * @return {@link BlacklistedWordsResponseDto}
	 */
	@GetMapping("/blacklistedwords/{langCode}")
	public BlacklistedWordsResponseDto getAllBlackListedWordByLangCode(@PathVariable("langCode") String langCode) {
		return blacklistedWordsService.getAllBlacklistedWordsBylangCode(langCode);
	}
}
