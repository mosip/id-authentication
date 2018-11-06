package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;

/**
 * 
 * @author Abhishek Kumar
 * @since 06-11-2018
 * @version 1.0.0
 */
@RestController
public class BlacklistedWordsController {
	@Autowired
	private BlacklistedWordsService blacklistedWordsService;

	@GetMapping("/blacklistedwords/{langcode}")
	public BlacklistedWordsResponseDto getAllBlackListedWordByLangCode(@PathVariable("langcode") String langCode) {
		return blacklistedWordsService.getAllBlacklistedWordsBylangCode(langCode);
	}
}
