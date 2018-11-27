package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonListRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonListResponseDto;
import io.mosip.kernel.masterdata.service.PacketRejectionReasonService;

@RestController
@RequestMapping(value = "/packetrejectionreasons")
public class PacketRejectionReasonController {
	/**
	 * creates instance of service class {@link PacketRejectionReasonService}
	 */
	@Autowired
	PacketRejectionReasonService reasonService;
	
	@PostMapping("/reasoncategory")
	public PostResponseDto saveReasonCategories(@RequestBody ReasonCategoryRequestDto requestDto) {
                
		return reasonService.saveReasonCategories(requestDto);
	}
	
	
	@PostMapping("/reasonlist")
	public ReasonListResponseDto saveReasonLists(@RequestBody ReasonListRequestDto requestDto) {
                
		return reasonService.saveReasonList(requestDto);
	}

	/**
	 * Get all reasoncategory list for the packet rejection reason
	 * 
	 * @return ReasonResponseDto
	 */
	@GetMapping
	public PacketRejectionReasonResponseDto getAllReasons() {

		return reasonService.getAllReasons();
	}

	@GetMapping(value = "/{reasoncategorycode}/{langcode}")
	public PacketRejectionReasonResponseDto getReasonsBasedOnReasonCatgCodeAndlangCode(@PathVariable("reasoncategorycode") String reasonCategoryCode,
			@PathVariable("langcode") String langCode) {
		
		return reasonService.getReasonsBasedOnLangCodeAndCategoryCode(reasonCategoryCode,langCode );

	}
}
