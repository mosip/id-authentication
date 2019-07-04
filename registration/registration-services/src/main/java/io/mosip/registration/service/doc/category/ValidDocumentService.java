package io.mosip.registration.service.doc.category;

import java.util.List;

import io.mosip.registration.dto.mastersync.DocumentCategoryDto;

/**
 * Service class {@code ValidDocumentService} is to access the Valid Documents
 * details
 * 
 * @author balamurugan.ramamoorthy
 * @since 1.0.0
 *
 */
public interface ValidDocumentService {

	/**
	 * This method is used to fetch all the configured valid documents for a
	 * specific applicant type,document category code and langcode
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