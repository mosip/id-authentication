package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryRequestDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;

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
	public DocumentCategoryResponseDto getAllDocumentCategory();

	/**
	 * To fetch all Document Category using language code
	 * 
	 * @param langCode
	 *            the language code
	 * @return {@linkplain DocumentCategoryDto}
	 */
	public DocumentCategoryResponseDto getAllDocumentCategoryByLaguageCode(String langCode);

	/**
	 * To fetch Document Category using id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain DocumentCategoryDto}
	 */
	public DocumentCategoryResponseDto getDocumentCategoryByCodeAndLangCode(String code, String langCode);

	public PostResponseDto addDocumentCategoriesData(DocumentCategoryRequestDto category);
}
