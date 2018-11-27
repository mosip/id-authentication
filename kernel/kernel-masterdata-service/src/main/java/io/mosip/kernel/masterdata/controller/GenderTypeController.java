package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.service.GenderTypeService;

/**
 * Controller class for fetching gender data from DB
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RestController
public class GenderTypeController {
	@Autowired
	private GenderTypeService genderTypeService;

	/**
	 * Get API to fetch all gender types
	 * 
	 * @return list of all gender types
	 */
	@GetMapping("/gendertype")
	public GenderTypeResponseDto getAllGenderType() {
		return genderTypeService.getAllGenderTypes();
	}

	/**
	 * Get API to fetch all gender types for a particular language code
	 * 
	 * @param langCode
	 *            the laguage code whose gender is to be returned
	 * @return list of all gender types for the given language code
	 */
	@GetMapping(value = "/gendertype/{langcode}")
	public GenderTypeResponseDto getGenderBylangCode(@PathVariable("langcode") String langCode) {
		return genderTypeService.getGenderTypeByLanguageCode(langCode);
	}

}
