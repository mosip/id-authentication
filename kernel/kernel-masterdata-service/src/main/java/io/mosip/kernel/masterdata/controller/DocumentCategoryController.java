package io.mosip.kernel.masterdata.controller;

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
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;

/**
 * Controller class to fetch or create document categories.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
public class DocumentCategoryController {

	@Autowired
	DocumentCategoryService documentCategoryService;

	/**
	 * API to fetch all Document categories details
	 * 
	 * @return All Document categories
	 */
	@GetMapping("/documentcategories")
	public DocumentCategoryResponseDto fetchAllDocumentCategory() {
		return documentCategoryService.getAllDocumentCategory();
	}

	/**
	 * API to fetch all Document categories details based on language code
	 * 
	 * @return All Document categories of a specific language
	 */
	@GetMapping("/documentcategories/{langcode}")
	public DocumentCategoryResponseDto fetchAllDocumentCategoryUsingLangCode(
			@PathVariable("langcode") String langCode) {
		return documentCategoryService.getAllDocumentCategoryByLaguageCode(langCode);
	}

	/**
	 * API to fetch A Document category details using id and language code
	 * 
	 * @return A Document category
	 */
	@GetMapping("/documentcategories/{code}/{langcode}")
	public DocumentCategoryResponseDto fetchDocumentCategoryUsingCodeAndLangCode(@PathVariable("code") String code,
			@PathVariable("langcode") String langCode) {
		return documentCategoryService.getDocumentCategoryByCodeAndLangCode(code, langCode);
	}

	/**
	 * This method creates document categories based on list provided.
	 * 
	 * @param category
	 *            the request dto.
	 * @return {@link PostResponseDto}
	 */
	@PostMapping("/documentcategories")
	public ResponseEntity<CodeAndLanguageCodeID> addDocumentCategories(
			@RequestBody RequestDto<DocumentCategoryDto> category) {
		return new ResponseEntity<>(documentCategoryService.addDocumentCategoriesData(category), HttpStatus.CREATED);

	}
}
