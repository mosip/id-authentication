package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.masterdata.dto.BlacklistedWordListRequestDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.WordAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller that provides with methods for operations on blacklisted words.
 * 
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestController
@Api(tags = { "BlacklistedWords" })
@RequestMapping("/blacklistedwords")
public class BlacklistedWordsController {
	@Autowired
	private BlacklistedWordsService blacklistedWordsService;

	/**
	 * Fetch the list of blacklisted words based on language code.
	 * 
	 * @param langCode
	 *            language code
	 * @return {@link BlacklistedWordsResponseDto}
	 */
	@ResponseFilter
	@GetMapping("/{langcode}")
	public BlacklistedWordsResponseDto getAllBlackListedWordByLangCode(@PathVariable("langcode") String langCode) {
		return blacklistedWordsService.getAllBlacklistedWordsBylangCode(langCode);
	}

	/**
	 * Takes the list of string as an argument and checks if the list contains any
	 * blacklisted words.
	 * 
	 * @param blacklistedwords
	 *            list of blacklisted words
	 * @return Valid if word does not belongs to black listed word and Invalid if
	 *         word belongs to black listed word
	 */
	@ResponseFilter
	@PostMapping(path = "/words", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.ALL_VALUE)
	@ApiOperation(value = "Black listed word validation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Valid Word"),
			@ApiResponse(code = 200, message = "Invalid Word") })
	public ResponseEntity<CodeResponseDto> validateWords(@RequestBody RequestWrapper<BlacklistedWordListRequestDto> blacklistedwords) {
		String isValid = "Valid";
		if (!blacklistedWordsService.validateWord(blacklistedwords.getRequest().getBlacklistedwords())) {
			isValid = "Invalid";
		}
		CodeResponseDto dto = new CodeResponseDto();
		dto.setCode(isValid);
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/**
	 * Method to add blacklisted word.
	 * 
	 * @param blackListedWordsRequestDto
	 *            the request dto that holds the blacklisted word to be added.
	 * @return the response entity i.e. the word and language code of the word
	 *         added.
	 */
	@ResponseFilter
	@PostMapping
	public ResponseEntity<WordAndLanguageCodeID> createBlackListedWord(
			@RequestBody @Valid RequestWrapper<BlacklistedWordsDto> blackListedWordsRequestDto) {
		return new ResponseEntity<>(
				blacklistedWordsService.createBlackListedWord(blackListedWordsRequestDto.getRequest()), HttpStatus.OK);
	}

	/**
	 * Method to update the blacklisted word
	 * 
	 * @param blackListedWordsRequestDto
	 *            the request dto that holds the blacklisted word to be updated .
	 * @return the response entity i.e. the word and language code of the word
	 *         updated.
	 */
	@ResponseFilter
	@PutMapping
	@ApiOperation(value = "update the blacklisted word", response = WordAndLanguageCodeID.class)
	public WordAndLanguageCodeID updateBlackListedWord(
			@Valid @RequestBody RequestWrapper<BlacklistedWordsDto> blackListedWordsRequestDto) {
		return blacklistedWordsService.updateBlackListedWord(blackListedWordsRequestDto.getRequest());
	}

	/**
	 * Method to deleted blacklisted word.
	 * 
	 * @param word
	 *            input blacklisted word to be deleted.
	 * @return deleted word.
	 */
	@ResponseFilter
	@DeleteMapping("/{word}")
	@ApiOperation(value = "delete the blacklisted word", response = WordAndLanguageCodeID.class)
	public ResponseEntity<CodeResponseDto> deleteBlackListedWord(@PathVariable("word") String word) {
		CodeResponseDto dto = new CodeResponseDto();//
		dto.setCode(blacklistedWordsService.deleteBlackListedWord(word));
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
}
