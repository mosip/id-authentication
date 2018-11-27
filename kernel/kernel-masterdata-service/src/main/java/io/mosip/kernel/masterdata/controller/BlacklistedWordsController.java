package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * controller with api's for blacklisted words
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
	@GetMapping("/blacklistedwords/{langcode}")
	public BlacklistedWordsResponseDto getAllBlackListedWordByLangCode(@PathVariable("langcode") String langCode) {
		return blacklistedWordsService.getAllBlacklistedWordsBylangCode(langCode);
	}

	/**
	 * Takes the list of string as an argument and checks if the list contains any
	 * blacklisted words.
	 * 
	 * @param blacklistedwords
	 * @return Valid if word does not belongs to black listed word and Invalid if
	 *         word belongs to black listed word
	 */
	@PostMapping(path = "/blacklistedwords/words", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.ALL_VALUE)
	@ApiOperation(value = "Black listed word validation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Valid Word"),
			@ApiResponse(code = 200, message = "Invalid Word") })
	public String validateWords(@RequestBody(required = true) List<String> blacklistedwords) {
		String isValid = "Valid";
		if (!blacklistedWordsService.validateWord(blacklistedwords)) {
			isValid = "Invalid";
		}
		return isValid;
	}

}
