package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/documentcategories")
public class DocumentCategoryController {

	@Autowired
	DocumentCategoryService documentCategoryService;

	/**
	 * API to fetch all Document categories details
	 * 
	 * @return All Document categories
	 */
	@GetMapping
	public List<DocumentCategoryDto> fetchAllDocumentCategory() {
		return documentCategoryService.getAllDocumentCategory();
	}

	/**
	 * API to fetch all Document categories details based on language code
	 * 
	 * @return All Document categories of a specific language
	 */
	@GetMapping("{languagecode}")
	public List<DocumentCategoryDto> fetchAllDocumentCategoryUsingLangCode(
			@PathVariable("languagecode") String langCode) {
		return documentCategoryService.getAllDocumentCategoryByLaguageCode(langCode);
	}

	/**
	 * API to fetch A Document category details using id and language code
	 * 
	 * @return A Document category
	 */
	@GetMapping("/{id}/{languagecode}")
	public DocumentCategoryDto fetchDocumentCategoryUsingCodeAndLangCode(@PathVariable("id") String code,
			@PathVariable("languagecode") String langCode) {
		return documentCategoryService.getDocumentCategoryByCodeAndLangCode(code, langCode);
	}
}
