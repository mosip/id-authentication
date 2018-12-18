package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.GenderTypeService;
import io.swagger.annotations.Api;

/**
 * Controller class for fetching gender data from DB
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "GenderType" })
public class GenderTypeController {
	@Autowired
	private GenderTypeService genderTypeService;

	/**
	 * Get API to fetch all gender types
	 * 
	 * @return list of all gender types
	 */
	@GetMapping("/v1.0/gendertype")
	public GenderTypeResponseDto getAllGenderType() {
		return genderTypeService.getAllGenderTypes();
	}

	/**
	 * Get API to fetch all gender types for a particular language code
	 * 
	 * @param langCode
	 *            the language code whose gender is to be returned
	 * @return list of all gender types for the given language code
	 */
	@GetMapping(value = "/v1.0/gendertype/{langcode}")
	public GenderTypeResponseDto getGenderBylangCode(@PathVariable("langcode") String langCode) {
		return genderTypeService.getGenderTypeByLangCode(langCode);
	}

	/**
	 * Post API to enter a new Gender Type Data
	 * 
	 * @param gender
	 *            input dto to enter a new gender data
	 * @return primary key of entered row of gender
	 */
	@PostMapping("/v1.0/gendertype")
	public ResponseEntity<CodeAndLanguageCodeID> saveGenderType(
			@Valid @RequestBody RequestDto<GenderTypeDto> gender) {
		return new ResponseEntity<>(genderTypeService.saveGenderType(gender), HttpStatus.CREATED);

	}

}
