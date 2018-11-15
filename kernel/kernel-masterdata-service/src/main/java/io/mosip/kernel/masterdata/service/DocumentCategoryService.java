package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
public interface DocumentCategoryService {

	/**
	 * To fetch all Document Category
	 * 
	 * @return {@linkplain DocumentCategoryDto}
	 */
	List<DocumentCategoryDto> getAllDocumentCategory();

	/**
	 * To fetch all Document Category using language code
	 * 
	 * @param langCode
	 *            the language code
	 * @return {@linkplain DocumentCategoryDto}
	 */
	List<DocumentCategoryDto> getAllDocumentCategoryByLaguageCode(String langCode);

	/**
	 * To fetch Document Category using id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain DocumentCategoryDto}
	 */
	DocumentCategoryDto getDocumentCategoryByCodeAndLangCode(String code, String langCode);

}
