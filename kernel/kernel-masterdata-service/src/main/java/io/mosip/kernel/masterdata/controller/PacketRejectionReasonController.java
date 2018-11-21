package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PacketRejectionReasonRequestDto;
import io.mosip.kernel.masterdata.dto.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.service.PacketRejectionReasonService;

@RestController
@RequestMapping(value = "/packetRejectionReasons")
public class PacketRejectionReasonController {
	/**
	 * creates instance of service class {@link PacketRejectionReasonService}
	 */
	@Autowired
	PacketRejectionReasonService reasonService;
	
	@PostMapping("/reasonCategory")
	public PacketRejectionReasonResponseDto saveReasonCategories(@RequestBody PacketRejectionReasonRequestDto requestDto) {
                
		return reasonService.saveReasonCategories(requestDto);
	}
	
	@PostMapping("/reasonList")
	public PacketRejectionReasonResponseDto saveReasonLists(@RequestBody PacketRejectionReasonRequestDto requestDto) {
                
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

	@GetMapping(value = "/{reasonCategoryCode}/{langCode}")
	public PacketRejectionReasonResponseDto getReasonsBasedOnReasonCatgCodeAndlangCode(@PathVariable String reasonCategoryCode,
			@PathVariable String langCode) {
		
		return reasonService.getReasonsBasedOnLangCodeAndCategoryCode(reasonCategoryCode,langCode );

	}
}
