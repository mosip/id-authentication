package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;
import io.mosip.kernel.masterdata.service.PacketRejectionReasonService;
import io.swagger.annotations.Api;

/**
 * This class handles the fecthing and creation of packetRejection reasons based
 * on category and its respective list
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "RejectionReason" })
@RequestMapping(value = "/packetrejectionreasons")
public class PacketRejectionReasonController {
	/**
	 * creates instance of service class {@link PacketRejectionReasonService}
	 */
	@Autowired
	private PacketRejectionReasonService reasonService;

	/**
	 * This API handles creation of reason categories
	 * 
	 * @param requestDto- reasoncategoryObject
	 * @return CodeAndLanguageCodeId
	 */
	@ResponseFilter
	@PostMapping("/reasoncategory")
	public ResponseWrapper<CodeAndLanguageCodeID> createReasonCategories(
			@Valid @RequestBody RequestWrapper<PostReasonCategoryDto> requestDto) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(reasonService.createReasonCategories(requestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * This API handles creation of reason list
	 * 
	 * @param requestDto -reasonListObject
	 * @return CodeLangCodeAndRsnCatCodeId
	 */
	@ResponseFilter
	@PostMapping("/reasonlist")
	public ResponseWrapper<CodeLangCodeAndRsnCatCodeID> createReasonLists(
			@Valid @RequestBody RequestWrapper<ReasonListDto> requestDto) {

		ResponseWrapper<CodeLangCodeAndRsnCatCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(reasonService.createReasonList(requestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Get all reasoncategory list for the packet rejection reason
	 * 
	 * @return ReasonResponseDto
	 */
	@ResponseFilter
	@GetMapping
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	public ResponseWrapper<PacketRejectionReasonResponseDto> getAllReasons() {

		ResponseWrapper<PacketRejectionReasonResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(reasonService.getAllReasons());
		return responseWrapper;
	}

	/**
	 * 
	 * @param reasonCategoryCode - reason category code
	 * @param langCode           - language code
	 * @return ReasonCategory- Reason category with reason list
	 */
	@ResponseFilter
	@GetMapping(value = "/{reasoncategorycode}/{langcode}")
	public ResponseWrapper<PacketRejectionReasonResponseDto> getReasonsBasedOnReasonCatgCodeAndlangCode(
			@PathVariable("reasoncategorycode") String reasonCategoryCode, @PathVariable("langcode") String langCode) {

		ResponseWrapper<PacketRejectionReasonResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(reasonService.getReasonsBasedOnLangCodeAndCategoryCode(reasonCategoryCode, langCode));
		return responseWrapper;
	}
}
