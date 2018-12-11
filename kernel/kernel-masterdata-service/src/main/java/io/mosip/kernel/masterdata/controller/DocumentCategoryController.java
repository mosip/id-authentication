package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.swagger.annotations.Api;

/**
 * Controller class to fetch or create document categories.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "DocumentCategory" })
public class DocumentCategoryController {

	@Autowired
	DocumentCategoryService documentCategoryService;

	/**
	 * API to fetch all Document categories details
	 * 
	 * @return All Document categories
	 */
	@GetMapping("/v1.0/documentcategories")
	public DocumentCategoryResponseDto getAllDocumentCategory() {
		return documentCategoryService.getAllDocumentCategory();
	}

	@GetMapping("/v1.0/documentcategories/{langcode}")
	public DocumentCategoryResponseDto getAllDocumentCategoryByLaguageCode(@PathVariable("langcode") String langCode) {
		return documentCategoryService.getAllDocumentCategoryByLaguageCode(langCode);
	}

	@GetMapping("/v1.0/documentcategories/{code}/{langcode}")
	public DocumentCategoryResponseDto getDocumentCategoryByCodeAndLangCode(@PathVariable("code") String code,
			@PathVariable("langcode") String langCode) {
		return documentCategoryService.getDocumentCategoryByCodeAndLangCode(code, langCode);
	}

	@PostMapping("/v1.0/documentcategories")
	public ResponseEntity<CodeAndLanguageCodeID> createDocumentCategory(
			@Valid @RequestBody RequestDto<DocumentCategoryDto> category) {
		return new ResponseEntity<>(documentCategoryService.createDocumentCategory(category), HttpStatus.CREATED);

	}
}
