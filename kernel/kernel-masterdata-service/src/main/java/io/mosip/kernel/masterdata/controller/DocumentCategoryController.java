package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class to fetch or create document categories.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@CrossOrigin
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

	/**
	 * API to fetch all Document categories details based on language code
	 * 
	 * @param langCode
	 *            the language code
	 * 
	 * @return {@link DocumentCategoryResponseDto}
	 */
	@GetMapping("/v1.0/documentcategories/{langcode}")
	public DocumentCategoryResponseDto getAllDocumentCategoryByLaguageCode(@PathVariable("langcode") String langCode) {
		return documentCategoryService.getAllDocumentCategoryByLaguageCode(langCode);
	}

	/**
	 * API to fetch all Document categories details based on code and language code
	 * 
	 * @param code
	 *            the code
	 * @param langCode
	 *            the language code
	 * @return {@link DocumentCategoryResponseDto}
	 */
	@GetMapping("/v1.0/documentcategories/{code}/{langcode}")
	public DocumentCategoryResponseDto getDocumentCategoryByCodeAndLangCode(@PathVariable("code") String code,
			@PathVariable("langcode") String langCode) {
		return documentCategoryService.getDocumentCategoryByCodeAndLangCode(code, langCode);
	}

	/**
	 * API to create Document category
	 * 
	 * @param category
	 *            is of type {@link DocumentCategoryDto}
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@PostMapping("/v1.0/documentcategories")
	@ApiOperation(value = "Service to create document category", notes = "Create document category and return composite id", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When document category successfully created", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No document category found"),
			@ApiResponse(code = 500, message = "While creating document category any error occured") })
	public ResponseEntity<CodeAndLanguageCodeID> createDocumentCategory(
			@ApiParam("Document category DTO to create") @Valid @RequestBody RequestDto<DocumentCategoryDto> category) {
		return new ResponseEntity<>(documentCategoryService.createDocumentCategory(category), HttpStatus.CREATED);
	}

	/**
	 * Api to update Document category.
	 * 
	 * @param category
	 *            is of type {@link DocumentCategoryDto}
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@PutMapping("/v1.0/documentcategories")
	@ApiOperation(value = "Service to update document category", notes = "Update document category and return composite id", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When document category successfully updated", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No document category found"),
			@ApiResponse(code = 500, message = "While updating document category any error occured") })
	public ResponseEntity<CodeAndLanguageCodeID> updateDocumentCategory(
			@ApiParam("Document category DTO to update") @Valid @RequestBody RequestDto<DocumentCategoryDto> category) {
		return new ResponseEntity<>(documentCategoryService.updateDocumentCategory(category), HttpStatus.OK);
	}

	/**
	 * Api to delete Document Category.
	 * 
	 * @param code
	 *            the document category code.
	 * @param langCode
	 *            the document category language code.
	 * @return the code.
	 */
	@DeleteMapping("/v1.0/documentcategories/{code}")
	@ApiOperation(value = "Service to delete document category", notes = "Delete document category and return composite id", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When document category successfully deleted", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When path is invalid"),
			@ApiResponse(code = 404, message = "When No document category found"),
			@ApiResponse(code = 500, message = "While deleting document category any error occured") })
	public ResponseEntity<CodeResponseDto> deleteDocumentCategory(@PathVariable("code") String code) {
		return new ResponseEntity<>(documentCategoryService.deleteDocumentCategory(code), HttpStatus.OK);
	}
}
