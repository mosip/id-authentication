package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.service.ApplicationService;

/**
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
	public List<ApplicationDto> fetchAllApplication() {
		return applicationService.getAllApplication();
	}
	
	/**
	 * API to fetch all Application types details based on language code
	 * 
	 * @return All Application details of specific language
	 */
	@GetMapping("/{languagecode}")
	public List<ApplicationDto> fetchAllApplicationByLanguageCode(@PathVariable("languagecode") String languageCode) {
		return applicationService.getAllApplicationByLanguageCode(languageCode);
	}
	
	/**
	 * API to fetch a application details using id and language code
	 * 
	 * @return An Application
	 */
	@GetMapping("/{id}/{languagecode}")
	public ApplicationDto fetchAllApplicationByCodeAndLanguageCode(@PathVariable("id") String code, @PathVariable("languagecode") String languageCode) {
		return applicationService.getApplicationByCodeAndLanguageCode(code, languageCode);
	}
}
