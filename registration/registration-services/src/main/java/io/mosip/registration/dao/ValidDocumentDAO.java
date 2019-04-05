package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.ApplicantValidDocument;

/**
 * Interface of ValidDocument
 * 
 * @author Brahmananda Reddy
 *
 */
public interface ValidDocumentDAO {


	/**
	 * Gets the valid documents.
	 *
	 * @param applicantType the applicant type
	 * @param docCategoryCode the doc category code
	 * @return the valid documents
	 */
	List<ApplicantValidDocument> getValidDocuments(String applicantType, String docCategoryCode);

}
