package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.ApplicantValidDocument;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
public interface ValidDocumentDAO {
	/**
	 * This method fetches the valid documents
	 * 
	 * @return {@link List} of valid documents
	 * 
	 */

	List<ApplicantValidDocument> getValidDocuments();

	List<ApplicantValidDocument> getValidDocuments(String applicantType, String docCategoryCode, String langCode);

}
