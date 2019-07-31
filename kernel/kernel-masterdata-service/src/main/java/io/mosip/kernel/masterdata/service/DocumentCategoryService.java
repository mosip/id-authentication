package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentCategoryExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * This interface have methods to fetch list of document category and to create
 * document categories based on list provided.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @author Uday Kumar
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
	public CodeAndLanguageCodeID createDocumentCategory(DocumentCategoryDto category);

	/**
	 * Method to update Document Category based on data provided.
	 * 
	 * @param category
	 *            the request dto.
	 * @return {@link CodeAndLanguageCodeID}
	 */
	public CodeAndLanguageCodeID updateDocumentCategory(DocumentCategoryDto category);

	/**
	 * Method to delete Document Category based on id provided.
	 * 
	 * @param code
	 *            the document category code.
	 * 
	 * @return {@link CodeResponseDto}
	 */
	public CodeResponseDto deleteDocumentCategory(String code);

	/**
	 * Method to fetch all Document category details
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the document categories
	 */
	public PageDto<DocumentCategoryExtnDto> getAllDocCategories(int pageNumber, int pageSize, String sortBy,
			String orderBy);
    /**
	 * Method to search Document Category.
	 * 
	 * @param dto
	 *            the searchDTO
	 * @return {@link PageResponseDto} containing pages of the searched values.
	 */
	public PageResponseDto<DocumentCategoryExtnDto> searchDocCategories(SearchDto dto);

	/**
	 * Method that returns the column values of specific filter column name.
	 * 
	 * @param filterValueDto
	 *            the request DTO that provides the column name.
	 * @return the response containing the filter values.
	 */
	public FilterResponseDto docCategoriesFilterValues(FilterValueDto filterValueDto);
}
