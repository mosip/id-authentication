package io.mosip.registration.service.impl;

import java.util.List;

import io.mosip.registration.dto.mastersync.DocumentCategoryDto;

/**
 * Service class for Documents
 * 
 * @author balamurugan.ramamoorthy
 *
 */
public interface ValidDocumentService {

	/**
	 * This method is used to fetch all the configured valid documents for a
	 * specific applicant type,document category code and lancode
	 * 
	 * @param applicantType
	 *            - applicant type
	 * @param docCode
	 *            - document category code
	 * @param langCode
	 *            - language code
	 * @return List - list of doc categories
	 */
	List<DocumentCategoryDto> getDocumentCategories(String applicantType, String docCode, String langCode);

}