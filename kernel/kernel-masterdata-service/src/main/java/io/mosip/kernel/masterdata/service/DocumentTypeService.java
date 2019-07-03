package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * This interface have methods to fetch list of valid document types and to
 * create document types based on list provided.
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface DocumentTypeService {
	/*
	 * methods to fetch list of valid document types for given document category
	 * code and language code
	 * 
	 * @param code
	 * 
	 * @param langCode
	 * 
	 * @return {@link List<DocumentTypeDto>}
	 * 
	 * 
	 */
	public List<DocumentTypeDto> getAllValidDocumentType(String code, String langCode);

	/**
	 * Method to create document type .
	 * 
	 * @param documentTypeDto
	 *            dto with documents type.
	 * @return {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID createDocumentType(DocumentTypeDto documentTypeDto);

	/**
	 * Method to update document type.
	 * 
	 * @param documentTypeDto
	 *            DTO of document type.
	 * @return {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID updateDocumentType(DocumentTypeDto documentTypeDto);

	/**
	 * Method to delete document type.
	 * 
	 * @param code
	 *            the document type code.
	 * @return {@link CodeResponseDto}.
	 */
	public CodeResponseDto deleteDocumentType(String code);

	/**
	 * This method provides with all document type
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
	 * @return the response i.e. pages containing the document type
	 */
	public PageDto<DocumentTypeExtnDto> getAllDocumentTypes(int pageNumber, int pageSize, String sortBy,
			String orderBy);
	
	/**
	 * Method to search and sort the filter based on the filters and sorting
	 * provided
	 * 
	 * @param dto
	 *            contains the data for searching and sorting.
	 * @return list of document type with all the metadata.
	 */
	public PageResponseDto<DocumentTypeExtnDto> searchDocumentTypes(SearchDto dto);
}