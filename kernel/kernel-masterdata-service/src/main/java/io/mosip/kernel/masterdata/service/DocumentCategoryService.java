package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * This interface have methods to fetch list of document category and to create
 * document categories based on list provided.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface DocumentCategoryService {

	/**
	 * Method to fetch all Document category details
	 * 
	 * @return DocumentCategoryDTO list
	 */
	public DocumentCategoryResponseDto getAllDocumentCategory();

	/**
	 * Method to fetch all Document category details based on language code
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return DocumentCategoryDTO list
	 */
	public DocumentCategoryResponseDto getAllDocumentCategoryByLaguageCode(String langCode);

	/**
	 * Method to fetch A Document category details based on id and language code
	 * 
	 * @param code
	 *            The Id of Document Category
	 * @param langCode
	 *            The language code
	 * @return DocumentCategoryDTO
	 */
	public DocumentCategoryResponseDto getDocumentCategoryByCodeAndLangCode(String code, String langCode);

	/**
	 * Method to create Document Categories based on list provided
	 * 
	 * @param category
	 *            dto with document categories list.
	 * @return {@linkplain CodeAndLanguageCodeID}
	 */
	public CodeAndLanguageCodeID createDocumentCategory(RequestDto<DocumentCategoryDto> category);
}
