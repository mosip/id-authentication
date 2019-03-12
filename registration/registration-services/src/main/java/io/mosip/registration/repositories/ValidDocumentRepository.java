package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ApplicantValidDocument;
import io.mosip.registration.entity.id.ApplicantValidDocumentID;

/**
 * 
 * @author Brahmananda Reddy
 *
 */

public interface ValidDocumentRepository extends BaseRepository<ApplicantValidDocument, ApplicantValidDocumentID> {

	List<ApplicantValidDocument> findByIsActiveTrueAndDocumentCategoryCodeAndDocumentCategoryLangCode(String docCategoryCode,
			String langCode);

	List<ApplicantValidDocument> findByValidDocumentIdAppTypeCodeAndDocumentCategoryCode(
			String applicantType, String docCategoryCode);
	
}
