package io.mosip.registration.service.impl;

import java.util.List;

import io.mosip.registration.dto.mastersync.DocumentCategoryDto;

/**
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
	 * @param docCategoryCode
	 * @param langCode
	 * @return
	 */
	List<DocumentCategoryDto> getDocumentCategories(String applicantType, String docCode, String langCode);

}