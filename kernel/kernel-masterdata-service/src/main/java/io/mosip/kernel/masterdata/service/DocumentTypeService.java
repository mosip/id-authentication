package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeRequestDto;
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
	 * Method to create list of document types .
	 * 
	 * @param documentTypeDto
	 *            dto with documents type list.
	 * @return {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID addDocumentTypes(DocumentTypeRequestDto documentTypeDto);

}
