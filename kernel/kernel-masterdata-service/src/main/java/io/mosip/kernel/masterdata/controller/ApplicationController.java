package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.ApplicationRequestDto;
import io.mosip.kernel.masterdata.dto.ApplicationResponseDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.service.ApplicationService;

/** 
 * Controller APIs to get Application types details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/applicationtypes")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;
	
	/**
	 * API to fetch all Application details
	 * 
	 * @return All Application details
	 */
	@GetMapping
	public ApplicationResponseDto fetchAllApplication() {
		return applicationService.getAllApplication();
	}
	
	/**
	 * API to fetch all Application types details based on language code
	 * 
	 * @return All Application details of specific language
	 */
	@GetMapping("/{langcode}")
	public ApplicationResponseDto fetchAllApplicationByLanguageCode(@PathVariable("langcode") String langCode) {
		return applicationService.getAllApplicationByLanguageCode(langCode);
	}
	
	/**
	 * API to fetch a application details using id and language code
	 * 
	 * @return An Application
	 */
	@GetMapping("/{code}/{langcode}")
	public ApplicationResponseDto fetchAllApplicationByCodeAndLanguageCode(@PathVariable("code") String code, @PathVariable("langcode") String langCode) {
		return applicationService.getApplicationByCodeAndLanguageCode(code, langCode);
	}
	
	/**
	 * API to add application details
	 * 
	 * @return PostResponseDto
	 */
	@PostMapping
	public CodeAndLanguageCodeId addApplication(@RequestBody ApplicationRequestDto application) {
		return applicationService.addApplicationData(application);
		
	}
}
