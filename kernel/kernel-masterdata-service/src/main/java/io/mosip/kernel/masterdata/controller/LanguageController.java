package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;
import io.mosip.kernel.masterdata.service.LanguageService;

/**
 * This class provide services to MOSIP system to do CRUD operations on
 * Languages.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/languages")
public class LanguageController {

	/**
	 * Service provide CRUD operation over Languages.
	 */
	@Autowired
	private LanguageService languageService;

	/**
	 * This method provides list of all languages present in MOSIP system.
	 * 
	 * @return LanguageRequestResponseDto
	 */
	@GetMapping
	public LanguageRequestResponseDto getAllLaguages() {
		return languageService.getAllLaguages();
	}

	/**
	 * This method provides list of all languages present in MOSIP system.
	 * 
	 * @return LanguageRequestResponseDto
	 */
	@PostMapping
	public LanguageRequestResponseDto saveAllLaguages(@RequestBody LanguageRequestResponseDto dto) {
		return languageService.saveAllLanguages(dto);
	}

}
