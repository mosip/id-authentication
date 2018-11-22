package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.ReasonResponseDto;
import io.mosip.kernel.masterdata.service.ReasonService;

@RestController
@RequestMapping(value = "/packetRejectionReasons")
public class PacketRejectionReasonController {
	/**
	 * creates instance of service class {@link ReasonService}
	 */
	@Autowired
	ReasonService reasonService;

	/**
	 * Get all reasoncategory list for the packet rejection reason
	 * 
	 * @return ReasonResponseDto
	 */
	@GetMapping
	public ReasonResponseDto getAllReasons() {

		return reasonService.getAllReasons();
	}

	@GetMapping(value = "/{reasonCategoryCode}/{languageCode}")
	public ReasonResponseDto getReasonsBasedOnReasonCatgCodeAndLanguageCode(@PathVariable String reasonCategoryCode,
			@PathVariable String languageCode) {
		
		return reasonService.getReasonsBasedOnLangCodeAndCategoryCode(reasonCategoryCode,languageCode );

	}
	@GetMapping(value = "/{languageCode}")
	public ReasonResponseDto getReasonsBasedOnLanguageCode(@PathVariable String reasonCategoryCode,
			@PathVariable String languageCode) {
		
		return reasonService.getReasonsBasedOnLangCodeAndCategoryCode(reasonCategoryCode,languageCode );

	}
}
