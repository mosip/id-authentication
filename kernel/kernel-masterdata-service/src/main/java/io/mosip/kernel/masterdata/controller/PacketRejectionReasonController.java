package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;
import io.mosip.kernel.masterdata.service.PacketRejectionReasonService;
/**
 * This class handles the fecthing and creation of packetRejection reasons based on category and 
 * its respsective list 
 * @author Srinivasan
 *
 */
@RestController
@RequestMapping(value = "/packetrejectionreasons")
public class PacketRejectionReasonController {
	/**
	 * creates instance of service class {@link PacketRejectionReasonService}
	 */
	@Autowired
	PacketRejectionReasonService reasonService;
	/**
	 * This API handles creation of reason categories 
	 * @param requestDto- reasoncategoryObject
	 * @return CodeAndLanguageCodeId-
	 */
	@PostMapping("/reasoncategory")
	public ResponseEntity<CodeAndLanguageCodeID> createReasonCategories(@Valid@RequestBody RequestDto<PostReasonCategoryDto> requestDto) {
                
		return new ResponseEntity<>(reasonService.createReasonCategories(requestDto),HttpStatus.CREATED);
	}
	
	/**
	 * This API handles creation of reason list
	 * @param requestDto -reasonListObject
	 * @return CodeLangCodeAndRsnCatCodeId
	 */
	@PostMapping("/reasonlist")
	public ResponseEntity<CodeLangCodeAndRsnCatCodeID> createReasonLists(@Valid@RequestBody RequestDto<ReasonListDto> requestDto) {
                
		return new ResponseEntity<>(reasonService.createReasonList(requestDto),HttpStatus.CREATED);
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

	/**
	 * 
	 * @param reasonCategoryCode
	 * @param langCode
	 * @return ReasonCategory- Reason cateogry with reason list
	 */
	@GetMapping(value = "/{reasoncategorycode}/{langcode}")
	public PacketRejectionReasonResponseDto getReasonsBasedOnReasonCatgCodeAndlangCode(@PathVariable("reasoncategorycode") String reasonCategoryCode,
			@PathVariable("langcode") String langCode) {
		
		return reasonService.getReasonsBasedOnLangCodeAndCategoryCode(reasonCategoryCode,langCode );

	}
}
