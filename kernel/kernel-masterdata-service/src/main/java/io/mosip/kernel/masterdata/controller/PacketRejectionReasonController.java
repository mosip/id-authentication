package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.ReasonListResponseDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;
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
	public PostResponseDto createReasonCategories(@Valid@RequestBody RequestDto<List<PostReasonCategoryDto>> requestDto) {
                
		return reasonService.createReasonCategories(requestDto);
	}
	
	
	@PostMapping("/reasonlist")
	public ReasonListResponseDto createReasonLists(@Valid@RequestBody RequestDto<List<ReasonListDto>> requestDto) {
                
		return reasonService.createReasonList(requestDto);
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
